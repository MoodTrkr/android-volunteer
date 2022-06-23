package com.example.moodtrackr.data

import java.util.*

data class SurveyData(
    val time: Date,
    val version: Int,
    val questions: MutableMap<Int, Int>
    )
{
    constructor(): this(Date(), 0, mutableMapOf<Int, Int>())
}
