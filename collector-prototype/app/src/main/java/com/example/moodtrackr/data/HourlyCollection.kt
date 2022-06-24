package com.example.moodtrackr.data

import java.util.*

data class HourlyCollection(
    val time: Date,
    val steps: Long,
    val unlocks: Long
    )
{
    constructor() : this(Date(), 0, 0)
}
