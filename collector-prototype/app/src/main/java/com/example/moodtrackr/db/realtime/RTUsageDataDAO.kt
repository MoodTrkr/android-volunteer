package com.example.moodtrackr.db.realtime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Date

@Dao
interface RTUsageDataDAO {

    @Query("SELECT u.usage_val FROM rtUsageRecords u WHERE u.date >= :start AND u.date <= :end")
    fun getAllInTimeRange(start: Long, end: Long): List<String>

    @Query("SELECT u.usage_val FROM rtUsageRecords u WHERE u.date >= :start AND u.date <= :end AND u.usage_var LIKE 'unlock' ")
    fun getAllUnlocksInTimeRange(start: Long, end: Long): List<String>

    @Insert
    fun insertAll(vararg usageRecord: RTUsageRecord)
}