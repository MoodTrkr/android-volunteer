package com.example.moodtrackr.data

import java.util.*

data class PeriodicCollection(
    val time: Date,
    val steps: Long,
    val unlocks: Long
    )
{
    constructor() : this(Date(), 0, 0)
}
