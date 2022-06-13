package com.example.moodtrackr.data

import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import java.util.*

data class SurveyData(
    val time: Date,
    val questions: Map<Int, Int>
    )
