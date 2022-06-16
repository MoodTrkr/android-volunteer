package com.example.moodtrackr.extractors.usage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.util.*
import java.util.concurrent.TimeUnit

class AppUsageExtractor(context: FragmentActivity?) {
    private lateinit var usm: UsageStatsManager
    private lateinit var context: Context

    init {
        this.context = context!!.applicationContext
        this.usm = context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    fun usageEventsQuery(): MutableMap<Long, Pair<String, Int>> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

        val queryResults: UsageEvents = usm.queryEvents(startTime, endTime)
        var event: UsageEvents.Event? = UsageEvents.Event()
        var filteredEvents = mutableMapOf<Long, Pair<String, Int>>()
        while (queryResults.hasNextEvent()) {
            queryResults.getNextEvent(event)
            if (event!!.eventType == UsageEvents.Event.ACTIVITY_PAUSED || event!!.eventType == UsageEvents.Event.ACTIVITY_RESUMED || event!!.eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
                filteredEvents[event!!.timeStamp] = Pair(event!!.packageName, event!!.eventType)
            }
        }
        return filteredEvents
    }

    fun usageStatsQuery(): MutableMap<String, Long> {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

//        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        var stats = mutableMapOf<String, Long>()
        var queryResults = usm.queryAndAggregateUsageStats(startTime, endTime)
        queryResults = queryResults.filter { (key, value) -> value.totalTimeInForeground>0}

        queryResults.forEach{(key, value) -> stats[key] = value.totalTimeInForeground }
        return stats
    }

    fun screenOnTimeQuery(): Long {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

        var screenTime: Long = 0
        val queryResults = usm.queryAndAggregateUsageStats(startTime, endTime)
        queryResults.forEach{(key, value) -> screenTime += value.totalTimeInForeground }
        return TimeUnit.MILLISECONDS.toMinutes(screenTime)

    }
}