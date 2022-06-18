package com.example.moodtrackr.db.realtime

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Date

@Dao
interface RTUsageDataDAO {

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date >= :start AND u.rt_date <= :end")
    suspend fun getAllInTimeRange(start: Long, end: Long): List<String>

    @Query("SELECT * FROM rt_usage_records u WHERE u.rt_date = :day AND u.rt_usage_var LIKE 'unlock' LIMIT 1")
    suspend fun getUnlockObjOnDay(day: Long): RTUsageRecord

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date = :day AND u.rt_usage_var LIKE 'unlock' LIMIT 1")
    suspend fun getUnlocksOnDay(day: Long): Long

    @Query("SELECT * FROM rt_usage_records u WHERE u.rt_date = :day AND u.rt_usage_var LIKE 'unlock' LIMIT 1")
    suspend fun getStepsObjOnDay(day: Long): RTUsageRecord

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date = :day AND u.rt_usage_var LIKE 'unlock' LIMIT 1")
    suspend fun getStepsOnDay(day: Long): Long

    @Query("SELECT u.rt_usage_val FROM rt_usage_records u WHERE u.rt_date >= :start AND u.rt_date <= :end AND u.rt_usage_var LIKE 'unlock' ")
    suspend fun getUnlocksInTimeRange(start: Long, end: Long): List<String>

    @Insert
    suspend fun insertAll(vararg usageRecord: RTUsageRecord)

    @Update
    suspend fun update(vararg usageRecord: RTUsageRecord)
}