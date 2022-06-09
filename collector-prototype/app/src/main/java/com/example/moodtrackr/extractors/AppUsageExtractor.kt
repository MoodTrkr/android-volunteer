package com.example.moodtrackr.extractors

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
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

    fun instantReturn(): MutableMap<String, UsageStats>? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

//        return usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return usm.queryAndAggregateUsageStats(startTime, endTime)
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