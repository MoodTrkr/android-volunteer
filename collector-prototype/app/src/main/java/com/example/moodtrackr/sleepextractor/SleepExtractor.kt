package com.example.moodtrackr.sleepextractor

import android.content.Context
import android.util.Log
import com.example.moodtrackr.collectors.db.DBHelper
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.*
import java.util.*

class SleepExtractor {
    companion object {

        /**
         *  Loads app usage for the day pointed to by days_offset
         *
         *  @param days_offset:Int
         *      This is used to select which day you want to
         *      collect the app usage data for. Offset starts
         *      from today being 0, and works into the past.
         *      For example, days_offset == 1 corresponds to
         *      yesterday, days_offset == 2 corresponds to
         *      the day before yesterday, etc...
         *
         *  @return app_usage: MutableMap<Long, MutableMap<String, String> >
         *      app_usage stores all states of every application
         *      over time. It takes on the following format:
         *          {Timestamp : {"first": "com.application.ID", "second": String(State)} }
         *
         *      Where State is an integer that is converted into a string (Must be able to convert
         *      back into an integer). Common States are "1", "2", "23"
         * */
        fun loadAppUsage(days_offset: Int, ctx: Context) : MutableMap<Long, MutableMap<String, String> >{ //TODO
            val dayTruncatedMillis = DatesUtil.getTodayTruncated().time - days_offset * 86400000
            val dayTruncated = DatesUtil.truncateDate(Date(dayTruncatedMillis + 5000))
            val record: MTUsageData = DBHelper.getObjSafe(ctx, dayTruncated)

            val appUsage = mutableMapOf<Long, MutableMap<String, String> >()
            for (entry in record.dailyCollection.usageLogs.data.iterator()) {
                appUsage.put(entry.key, mutableMapOf(
                    Pair("first", entry.value.first),
                    Pair("second", entry.value.second.toString())))
            }
            return appUsage
        }




        /**
         *  Merges today and yesterday's app usage data
         *
         *  @param today: MutableMap<Long, MutableMap<String, String> >
         *      A Map representing today's app usage that takes on
         *      the following format:
         *          {Timestamp : {"first": "com.application.ID", "second": String(State)} }
         *  @param yesterday: MutableMap<Long, MutableMap<String, String> >
         *      A Map representing yesterday's app usage that takes on
         *      the following format:
         *          {Timestamp : {"first": "com.application.ID", "second": String(State)} }
         *
         *  @return: merged: MutableMap<Long, MutableMap<String, String> >
         *      A Map representing today and yesterday's app usage merged together.
         *      It takes on the following format:
         *          {Timestamp : {"first": "com.application.ID", "second": String(State)} }
         *
         * */
        fun joinTodayYesterday(today: MutableMap<Long, MutableMap<String, String> >,
                               yesterday: MutableMap<Long, MutableMap<String, String> >?): MutableMap<Long, MutableMap<String, String> > {


            var merged: MutableMap<Long, MutableMap<String, String> > = mutableMapOf()
            merged = (today.toMap() + yesterday!!.toMap()).toMutableMap()
            return merged
        }


        /**
         *  Converts Timecode-indexed map to App Bundle ID-indexed map
         *
         *  @param app_usage: MutableMap<Long, MutableMap<String, String> >
         *      App usage map containing data from today and yesterday.
         *      This input could be the output of joinTodayYesterday().
         *
         *  @return apps: MutableMap<String, Pair<MutableList<Long>, MutableList<Int> > >
         *      A Map where each app ID is mapped to its time-series relation:
         *          {"com.app.bundleID" : ([x1, ..., xn], [y1, ..., yn])}
         *     Where [x] is the timecode, [y] is the application state {1, 2, 23}
         *
         * */
        fun indexApps(app_usage: MutableMap<Long, MutableMap<String, String> >) :
                MutableMap<String, Pair<MutableList<Long>, MutableList<Int> > > {


            // {"com.app.bundleID" : ([x1, ..., xn], [y1, ..., yn])}
            // Where [x] is the timecode, [y] is the application state {1, 2, 23}
            var apps: MutableMap<String, Pair<MutableList<Long>, MutableList<Int> > > = mutableMapOf()
            var keys = app_usage.keys.toList().toMutableList()
            var i = 0

            // Remove all entries that are 1.4 days older than the most recent entry
            while (i < keys.size) {
                if (keys[i] < keys[keys.size-1] - 120960000) {
                    keys.removeAt(i)
                } else {
                    i += 1
                }
            }

            // populate apps (Find definition of apps for an overview of the data structure)
            for (key in keys) {
                if (!apps.containsKey(key = app_usage[key]!!["first"].toString()) ) {
                    apps[app_usage[key]!!["first"]!!] = Pair(mutableListOf(key), mutableListOf(app_usage[key]!!["second"]!!.toInt()))
                } else {
                    apps[app_usage[key]!!["first"]!!]!!.first.add(key)
                    apps[app_usage[key]!!["first"]!!]!!.second.add(app_usage[key]!!["second"]!!.toInt())
                }
            }

            return apps

        }



        /**
         * Merges all individual application time series to two MutableLists with the
         * following properties:
         *      Time codes: this will be a MutableList<Long> where each index represents
         *          1 second of time. Each element will store a time code representing the
         *          number of milliseconds from the unix epoch.
         *
         *      Application Activity: This will be a MutableList<Int> where each index
         *          represents 1 second of time. Each element will store the number of
         *          application activity states that happened in that second.
         *
         * @param app_stats: MutableMap<String, Pair<MutableList<Long>, MutableList<Int> > >:
         *      A Map where each app ID is mapped to its time-series relation:
         *          {"com.app.bundleID" : ([x1, ..., xn], [y1, ..., yn])}
         *     Where [x] is the timecode, [y] is the application state {1, 2, 23}
         *
         * @return Pair<MutableList<Long>, MutableList<Int> >:
         *      Two lists:
         *          x: sorted list of time codes
         *          y: list of corresponding application activity
         * */
        fun appStatsToTimeSeries(app_stats: MutableMap<String, Pair<MutableList<Long>, MutableList<Int> > >)
            :Pair<MutableList<Long>, MutableList<Float> > {
            // First, store all timecodes of all apps
            // into a single mutable list
            var x = mutableListOf<Long>()
            for (entry in app_stats.entries.iterator()) {
                for (timecode in app_stats[entry.key]!!.first) {
                    x.add(timecode)
                }
                for (state in app_stats[entry.key]!!.second) { //TODO delete
//                    Log.e("App Y1...Yn", state.toString())
                }
            }

            var first = (x.minOrNull() ?: 0).toLong() / 1000L * 1000L
            var last = (x.maxOrNull() ?: 0).toLong() / 1000L * 1000L

            Log.e("FIRST", first.toString())
            Log.e("LAST", last.toString())

            var bins = mutableMapOf<Long, Int>()

            for (i in first..last step 1000) {
                bins[i] = 0
            }


            // count the occurrences of each bin.
            for (item in x) {
                if (!bins.containsKey(item / 1000L * 1000L)) {
                    bins[item / 1000L * 1000L] = 1
                } else {
                    bins[item / 1000L * 1000L] = bins[item / 1000L * 1000L]!! + 1
                }
            }

            // x: time, which is a sorted list of time steps
            // y: values, which are the occurrences at each time step
            var times = bins.keys.sorted()
            var values = mutableListOf<Float>()
            for (time in times) {
                values.add(bins[time]!!.toFloat())
            }

            Log.e("VALUES BEFORE MOVING AVERAGE", values.toString()) //TODO delete



            return Pair(times.toMutableList(), movingAverage(values, 500))
        }


        /**
         * Obtains candidate sleep bounds via thresholding.
         *
         * @param app_activity: MutableList<Int>:
         *      A time-series list containing the amount of activity
         *      at each index. This list should correspond to a MutableList<Long>
         *      containing the timestamps
         * @param thresh: Float
         *      A float between 0 and 1 to for thresholding.
         * @returns candidate_regions: MutableList<Boolean>:
         *      A boolean mask corresponding to app_activity
         * */
        fun getCandidateRegions(app_activity: MutableList<Float>, thresh: Float) : MutableList<Boolean> {

            // define bounds and thresholding cutoff
            val maxMin = maxmin(app_activity)
            val mx = maxMin.second
            val mn = maxMin.first
            val rnge = mx - mn

            val dbound = mn + rnge * thresh

            val candidateRegions = mutableListOf<Boolean>()
            // create boolean mask
            for (i in app_activity.indices) {
                candidateRegions.add(app_activity[i] < dbound)
            }

            Log.e("APP ACTIVITY", app_activity.toString())


            return candidateRegions
        }

        /**
         * Computes the sleep bounds by finding the longest section of
         * true in a boolean array.
         *
         * @param x: MutableList<Boolean>:
         *      Boolean array
         * @return Pair<Int, Int>:
         *      The indices to the boolean array (and consequently, to the time code array
         *      as well as to the activity array) that indicate the start and stop of the
         *      sleep interval.
         * */
        fun computeBounds(x: MutableList<Boolean>) : Pair<Int, Int>? {
            var longestIdx = 0
            val ranges = mutableListOf<Pair<Int, Int> >()
            var sectionStart = -1
            var sectionEnd = -1

            for (i in x.indices) {
                Log.e("X", x[i].toString())
            }

            for (i in x.indices) {
                if (x[i]) {
                    // set start to current index, prepare end to receive index later
                    if (sectionStart == -1) {
                        sectionStart = i
                        sectionEnd = -1
                    }
                } else {
                    if (sectionEnd == -1 && sectionStart != -1) {
                        sectionEnd = i
                        ranges.add(Pair(sectionStart, sectionEnd))

                        if (ranges.size > 1) {
                            //if the new range is longer than the longest range,
                            // set the longest range as the new range
                            if (ranges.last().second-ranges.last().first >
                                ranges[longestIdx].second-ranges[longestIdx].first){

                                longestIdx = ranges.size - 1
                            }
                        }
                        sectionStart = -1
                    }
                }

                // If there is a positive range all the way till the end...
                if (i == x.size-1 && sectionStart != -1) {
                    ranges.add(Pair(sectionStart, x.size-1))
                    if (ranges.size > 1) {
                        if (ranges.last().second-ranges.last().first >
                            ranges[longestIdx].second-ranges[longestIdx].first){

                            longestIdx = ranges.size - 1
                        }
                    }
                }
            }
            try {
                Log.e("BOUNDS", ranges[longestIdx].toString())
                return ranges[longestIdx]
            } catch (e: Exception) {
                Log.e("ranges[longestIdx] not found", e.toString())
                return null
            }

        }

        /**
         * Compute the Sleep Wake Bounds from app activities
         *
         * @param app_activity_today: MutableList<Int>:
         *      Time-series list containing the number of app activities
         *      per second. Every new index is one second in time.
         * @param time_codes_today: MutableList<Long>:
         *      Time-series list containing the number of time codes
         *      per second. Every new index is one second in time.
         *
         *
         * @return Pair<Long, Long>:
         *      starting time code, ending time code
         *
         * */
        fun sleepWakeBoundsFromActivity(app_activity: MutableList<Float>,
                            time_codes: MutableList<Long>) : Pair<Long, Long> {


            val regions = getCandidateRegions(app_activity, thresh=0.1F)
            val start_stop_idx = computeBounds(regions)

            Log.e("TIMECODES", Pair(time_codes[start_stop_idx!!.first], time_codes[start_stop_idx!!.second]).toString())
            return Pair(time_codes[start_stop_idx!!.first], time_codes[start_stop_idx!!.second])
        }



        /**
         * This function computes the sleep boundaries of a certain day.
         * You specify which day by using the days_offset parameter
         *
         * @param days_offset: Int:
         *      number of days offset from today. This param must be positive.
         *      For example, today == 0, yesterday == 1, the day before yesterday == 2,
         *      etc, etc.
         *
         * @param ctx: Context:
         *      Context.
         *
         * @return Pair<Long, Long>:
         *      the starting time code as well as the stop timecode, indicating
         *      the range in time where the person is asleep
         * */
        fun computeSleepBounds(days_offset: Int, ctx: Context) : Pair<Long, Long> {
            val appUsageToday = loadAppUsage(days_offset, ctx)
            val appUsageYesterday = loadAppUsage(days_offset-1, ctx)

            val appUsage = joinTodayYesterday(today = appUsageToday, yesterday = appUsageYesterday)
            val appStats = indexApps(appUsage)

            val timeActivity = appStatsToTimeSeries(appStats)
            return sleepWakeBoundsFromActivity(app_activity = timeActivity.second, time_codes = timeActivity.first)
        }


        /**
         * This function computes the sleep boundaries of a certain day.
         * You specify which day by using the days_offset parameter.
         * This function runs Asynchronously
         *
         * @param days_offset: Int:
         *      number of days offset from today. This param must be positive.
         *      For example, today == 0, yesterday == 1, the day before yesterday == 2,
         *      etc, etc.
         *
         * @param ctx: Context:
         *      Context.
         *
         * @return Pair<Long, Long>:
         *      the starting time code as well as the stop timecode, indicating
         *      the range in time where the person is asleep
         * */
        fun computeSleepBoundsAsync(days_offset: Int, ctx: Context) : CompletableDeferred<Pair<Long, Long> >{
            val deferred = CompletableDeferred<Pair<Long, Long>>()
            CoroutineScope(Dispatchers.Default).launch {
                val appUsageToday = loadAppUsage(days_offset, ctx)
                val appUsageYesterday = loadAppUsage(days_offset-1, ctx)

                val appUsage = joinTodayYesterday(today = appUsageToday, yesterday = appUsageYesterday)
                val appStats = indexApps(appUsage)

                val timeActivity = appStatsToTimeSeries(appStats)
                val computed = sleepWakeBoundsFromActivity(app_activity = timeActivity.second, time_codes = timeActivity.first)
                deferred.complete(computed)
            }
            return deferred
        }

        /////////////////////////////////////////
        /////////////////////////////////////////
        //////////////// Utils //////////////////
        /////////////////////////////////////////
        /////////////////////////////////////////



        /**
         * Computes the Moving Average of a series
         *
         * @param seq: List<T>:
         *      Input sequence
         * @param window_size: Int:
         *      How many elements of the array to average across
         * */
        fun movingAverage(seq: List<Float>, window_size: Int): MutableList<Float> {
            val y = mutableListOf(0.0F)
            val b = 1.0-1.0/(window_size).toFloat()

            for (i in 1 until seq.size) {
                y.add((b * (y[i-1].toFloat()) + (1-b) * seq[i].toFloat()).toFloat())
            }

            return y
        }


        /**
         * Computes the max value and the min value in a
         * List.
         *
         * @param x: MutableList<T>:
         *      A list of items that are comparable.
         *
         * @return Pair<T, T>:
         *      min value, max value
         *
         * */
        fun <T> maxmin(x: MutableList<T>): Pair<T, T> {
            var max_seen = x[0]!!
            var min_seen = x[0]!!

            for (i in x.indices) {
                if (x[i] > max_seen) {
                    max_seen = x[i]!!
                }
                if (x[i] < min_seen) {
                    min_seen = x[i]!!
                }
            }
            return Pair(min_seen, max_seen)
        }
    }

}

private operator fun <T> T.compareTo(other: T): Int {
    return this.hashCode() - other.hashCode()
}
