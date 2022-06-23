package com.example.moodtrackr.extractors.usage.data

import androidx.room.ColumnInfo
import java.util.*

data class MTAppUsageLogs(
    val data: MutableMap<Long, Pair<String, Int>>
    )
{
    constructor(): this(mutableMapOf<Long, Pair<String, Int>>())
}
