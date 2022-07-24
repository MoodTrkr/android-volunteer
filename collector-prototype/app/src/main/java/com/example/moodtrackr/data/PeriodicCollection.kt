package com.example.moodtrackr.data

import java.util.*

data class PeriodicCollection(
    val time: Long,
    val steps: Long,
    val unlocks: Long
    )
{
    constructor() : this(0, 0, 0)
}
