package com.example.moodtrackr.data

import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats

data class DailyCollection(val usageData: MTAppUsageStats, val callLogs: MTCallStats, val screenTime: Long)
