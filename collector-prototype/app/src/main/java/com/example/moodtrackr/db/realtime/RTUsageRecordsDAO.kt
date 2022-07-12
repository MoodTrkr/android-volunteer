package com.example.moodtrackr.db.realtime

import androidx.room.*
import java.sql.Date

@Dao
interface RTUsageRecordsDAO {

    @Query("SELECT * FROM rt_usage_records u WHERE u.rt_date >= :start AND u.rt_date <= :end")
    suspend fun getAllInTimeRange(start: Long, end: Long): List<RTUsageRecord>?

    @Query("SELECT * FROM rt_usage_records u WHERE u.rt_date = :day LIMIT 1")
    suspend fun getObjOnDay(day: Long): RTUsageRecord?

    @Query("SELECT * FROM rt_usage_records u")
    suspend fun getAll(): RTUsageRecord?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg usageRecord: RTUsageRecord)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(vararg usageRecord: RTUsageRecord)
}