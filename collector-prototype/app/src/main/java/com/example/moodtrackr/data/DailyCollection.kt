package com.example.moodtrackr.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageLogs
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats


data class DailyCollection(
    @Embedded(prefix = "app_usage_stats_") val usageStats: MTAppUsageStats,
    @Embedded(prefix = "app_usage_logs_") val usageLogs: MTAppUsageLogs,
    @Embedded(prefix = "call_stats_") val callLogs: MTCallStats,
    val screenTime: Long = 0
    )
{
    constructor(): this(MTAppUsageStats(), MTAppUsageLogs(), MTCallStats())
}
