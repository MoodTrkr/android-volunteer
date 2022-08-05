package com.example.moodtrackr.data

import androidx.room.Embedded
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import java.util.*

data class PeriodicCollection(
    val time: Long,
    @Embedded(prefix = "app_usage_stats_") var usageStats: MTAppUsageStats,
    val steps: Long,
    val unlocks: Long
    )
{
    constructor() : this(0, MTAppUsageStats(),0, 0)
}
