package com.example.moodtrackr.db.router

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject
import java.util.*

@Entity(tableName = "mt_router_requests")
data class RouterRequest constructor(
    @PrimaryKey @ColumnInfo(name = "date") var date: Date = Date(),
    @ColumnInfo(name = "type") var type: String = "",
    @ColumnInfo(name = "params") var params: String = "",
    @ColumnInfo(name = "body") var payload: String = ""
)