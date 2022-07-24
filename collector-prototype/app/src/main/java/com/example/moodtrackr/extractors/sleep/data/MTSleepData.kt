package com.example.moodtrackr.extractors.sleep.data

import java.util.*

data class MTSleepData(
    val start: Long,
    val end: Long
)
{
    constructor() : this(0, 0)
}
