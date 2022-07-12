package com.example.moodtrackr.data

import java.util.*

data class SurveyData(
    val time: Long,
    val version: Int,
    val questions: MutableMap<Int, Int>,
    var complete: Boolean
    )
{
    constructor(): this(Date().time, 0, mutableMapOf<Int, Int>(), false)
    constructor(date: Date, version: Int, questions: MutableMap<Int, Int>, complete: Boolean): this(date.time, version, questions, complete)
}
