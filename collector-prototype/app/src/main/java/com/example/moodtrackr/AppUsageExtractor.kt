package com.example.moodtrackr

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*


class AppUsageExtractor {
    fun instantReturn(context: Context): UsageEvents {
        val cal: Calendar = Calendar.getInstance()
        val startTime: Long = cal.timeInMillis - 2 * 24 * 60 * 60 * 1000
        val endTime: Long = cal.timeInMillis
        val usm: UsageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        return usm.queryEvents(startTime, endTime)
    }
}