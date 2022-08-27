package com.example.moodtrackr.db.records

import androidx.room.*
import com.example.moodtrackr.data.MTUsageData

@Dao
interface UsageRecordsDAO {

    @Query("SELECT * FROM usage_records u WHERE u.date >= :start AND u.date <= :end")
    suspend fun getByTimeRange(start: Long, end: Long): List<MTUsageData>

    @Query("SELECT * FROM usage_records u WHERE u.date = :day LIMIT 1")
    suspend fun getObjOnDay(day: Long): MTUsageData?

    @Query("SELECT * FROM usage_records")
    suspend fun getAll(): List<MTUsageData>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usageRecord: MTUsageData)

    @Update
    suspend fun update(vararg usageRecord: MTUsageData)
}