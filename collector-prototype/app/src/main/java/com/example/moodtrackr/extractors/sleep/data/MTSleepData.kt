package com.example.moodtrackr.extractors.sleep.data

import java.util.*

data class MTSleepData(
    var start: Long,
    var end: Long
)
{
    constructor() : this(0, 0)
}
