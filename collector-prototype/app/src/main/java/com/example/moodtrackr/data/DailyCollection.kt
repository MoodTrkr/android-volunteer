package com.example.moodtrackr.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats


data class DailyCollection(
    @Embedded(prefix = "app_usage_") val usageData: MTAppUsageStats,
    @Embedded(prefix = "call_stats_") val callLogs: MTCallStats,
    val screenTime: Long = 0
    )
{
    constructor(): this(MTAppUsageStats(), MTCallStats())
}
