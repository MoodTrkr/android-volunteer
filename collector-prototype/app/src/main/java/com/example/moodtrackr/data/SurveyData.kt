package com.example.moodtrackr.data

import java.util.*

data class SurveyData(
    val time: Date,
    val questions: MutableMap<Int, Int>,
    val version: Int,
    )
{
    constructor(): this(Date(), mutableMapOf<Int, Int>(),0)
}
