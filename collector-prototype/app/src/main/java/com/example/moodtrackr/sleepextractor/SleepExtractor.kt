package com.example.moodtrackr.sleepextractor

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
        fun loadAppUsage(days_offset: Int) { //TODO

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
                               yesterday: MutableMap<Long, MutableMap<String, String> >): MutableMap<Long, MutableMap<String, String> > {


            var merged: MutableMap<Long, MutableMap<String, String> > = mutableMapOf()
            merged = (today.toMap() + yesterday.toMap()).toMutableMap()
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
                if (!apps.containsKey(key = app_usage[key]!!["first"] as String) ) {
                    apps.put(app_usage[key]!!["first"]!!, Pair(mutableListOf(key), mutableListOf(app_usage[key]!!["second"]!!.toInt())))
                } else {
                    apps[app_usage[key]!!["first"]!!]!!.first.add(key)
                    apps[app_usage[key]!!["first"]!!]!!.second.add(app_usage[key]!!["second"]!!.toInt())
                }
            }

            return apps

        }



        /**
         * Merges all individual application time series to a single, large time series
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
            :Pair<MutableList<Long>, MutableList<Int> > {
            // First, store all timecodes of all apps
            // into a single mutable list
            var x = mutableListOf<Long>()
            for (entry in app_stats.entries.iterator()) {
                for (timecode in app_stats[entry.key]!!.first) {
                    x.add(timecode)
                }
            }

            var first = (x.minOrNull() ?: 0).toLong() / 1000L * 1000L
            var last = (x.maxOrNull() ?: 0).toLong() / 1000L * 1000L

            var bins = mutableMapOf<Long, Int>()

            for (i in first..last step 1000) {
                bins[i] = 0
            }


            // count the occurrences of each bin.
            for (item in x) {
                if (!bins.containsKey(item / 1000L * 1000L)) {
                    bins.put(key = item / 1000L * 1000L, value = 1)
                } else {
                    bins[item / 1000L * 1000L] = bins[item / 1000L * 1000L]!! + 1
                }
            }

            // x: time, which is a sorted list of time steps
            // y: values, which are the occurrences at each time step
            var times = bins.keys.sorted()
            var values = mutableListOf<Int>()
            for (time in times) {
                values.add(bins[time]!!)
            }



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
        fun getCandidateRegions(app_activity: MutableList<Int>, thresh: Float) : MutableList<Boolean> {

            // define bounds and thresholding cutoff
            val max_min = maxmin(app_activity)
            val mx = max_min.second
            val mn = max_min.first
            val rnge = mx - mn

            val dbound = mn + rnge * thresh

            val candidate_regions = mutableListOf<Boolean>()
            // create boolean mask
            for (i in app_activity.indices) {
                candidate_regions.add(app_activity[i]!! < dbound)
            }
            return candidate_regions
        }

        /**
         * Computes the sleep bounds by finding the longest section of
         * true in a boolean array.
         *
         * @param x: MutableList<Boolean>:
         *      Boolean array
         *
         * */
        fun computeBounds(x: MutableList<Boolean>) {
            var longest_idx = 0
            var ranges = mutableListOf<Pair<Int, Int> >()
            var section_start = -1
            var section_end = -1

            for (i in x.indices) {
                if (x[i]) {
                    // set start to current index, prepare end to receive index later
                    if (section_start == -1) {
                        section_start = i
                        section_end = -1
                    }
                } else {
                    if (section_end == -1 && section_start != -1) {
                        section_end = i
                        ranges.add(Pair(section_start, section_end))

                        if (ranges.size > 1) {
                            
                        }
                    }
                }
            }
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
        fun <T> movingAverage(seq: List<T>, window_size: Int): MutableList<T> {
            var y = mutableListOf<T>(0 as T)
            var b = 1.0-1.0/(window_size).toFloat()

            for (i in 1 until seq.size) {
                y.add((b * (y[i-1] as Float) + (1-b) * seq[i] as Float) as T)
            }

            return y
        }


        /**
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
