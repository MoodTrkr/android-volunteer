package com.example.moodtrackr.db.realtime

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.*

@Entity(tableName = "RTUsageRecords")
data class RTUsageRecord constructor(
    @PrimaryKey @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "usage_var") var usageVar: String,
    @ColumnInfo(name = "usage_val") var usageVal: String
)