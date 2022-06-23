package com.example.moodtrackr.extractors.usage.data

import androidx.room.ColumnInfo
import java.util.*

data class MTAppUsageStats(
    val data: MutableMap<String, Long>
    )
{
    constructor(): this(mutableMapOf<String, Long>())
}
