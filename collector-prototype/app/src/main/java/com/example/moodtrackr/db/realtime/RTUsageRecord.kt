package com.example.moodtrackr.db.realtime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.*

@Entity(tableName = "rt_usage_records")
data class RTUsageRecord constructor(
    @PrimaryKey @ColumnInfo(name = "rt_date") var date: Date = Date(),
    @ColumnInfo(name = "rt_unlocks") var unlocks: Long = 0,
    @ColumnInfo(name = "rt_steps") var steps: Long = 0
)