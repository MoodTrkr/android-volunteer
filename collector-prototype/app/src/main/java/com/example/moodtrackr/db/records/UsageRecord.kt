package com.example.moodtrackr.db.records

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.*

@Entity(tableName = "usageRecords")
data class UsageRecord constructor(
    @PrimaryKey @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "usage_data") var variable: String
)