package com.example.moodtrackr.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.*

@Entity
data class UsageData constructor(
    @ColumnInfo(name = "date") var date: Date,
    @ColumnInfo(name = "usage_var") var variable: String,
    @ColumnInfo(name = "usage_val") var value: String,
)