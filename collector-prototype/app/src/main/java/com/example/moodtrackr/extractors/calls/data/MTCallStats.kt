package com.example.moodtrackr.extractors.calls.data

import java.util.*

data class MTCallStats(
    val calls: MutableMap<Long, Long> //actually supposed to represent <Date, Long>
    )
{
    constructor(): this(mutableMapOf<Long, Long>())
}