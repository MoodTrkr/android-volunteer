package com.example.moodtrackr.data

import androidx.room.Embedded
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import java.util.*

data class SurveyData(
    val time: Long,
    val version: Int,
    val questions: MutableMap<Int, Int>,
    var complete: Boolean,
    @Embedded(prefix = "sleep_") var sleepData: MTSleepData
)
{
    constructor(): this(Date().time, 0, mutableMapOf<Int, Int>(), false, MTSleepData())
    constructor(date: Date, version: Int, questions: MutableMap<Int, Int>, complete: Boolean, sleepData: MTSleepData): this(date.time, version, questions, complete, sleepData)
}
