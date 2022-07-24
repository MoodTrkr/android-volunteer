package com.example.moodtrackr.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.extractors.usage.data.MTAppUsageLogs
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import java.util.*


data class DailyCollection(
    var date: Long,
    @Embedded(prefix = "app_usage_stats_") var usageStats: MTAppUsageStats,
    @Embedded(prefix = "app_usage_logs_") var usageLogs: MTAppUsageLogs,
    @Embedded(prefix = "call_stats_") var callLogs: MTCallStats,
    @Embedded(prefix = "sleep_") var sleepData: MTSleepData,
    var screenTime: Long = 0,
    var complete: Boolean = false
    )
{
    constructor(): this(Date().time, MTAppUsageStats(), MTAppUsageLogs(), MTCallStats(), MTSleepData(),0,false)
}
