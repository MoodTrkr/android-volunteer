package com.example.moodtrackr.db.realtime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Date

@Dao
interface RTUsageDataDAO {

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date >= :start AND u.rt_date <= :end")
    fun getAllInTimeRange(start: Long, end: Long): List<String>

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date >= :start AND u.rt_date <= :end AND u.rt_usage_var LIKE 'unlock' ")
    fun getAllUnlocksInTimeRange(start: Long, end: Long): List<String>

    @Insert
    fun insertAll(vararg usageRecord: RTUsageRecord)
}