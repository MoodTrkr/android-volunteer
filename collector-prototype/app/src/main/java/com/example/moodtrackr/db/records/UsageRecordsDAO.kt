package com.example.moodtrackr.db.records

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moodtrackr.data.MTUsageData
import java.sql.Date

@Dao
interface UsageRecordsDAO {

    @Query("SELECT * FROM usage_records u WHERE u.date >= :start AND u.date <= :end")
    suspend fun getByTimeRange(start: Long, end: Long): List<MTUsageData>

//    @Query("SELECT strftime('%d', :date) FROM usageRecords")
//    fun getByDate(date: Date): List<MTUsageData>

    @Query("SELECT * FROM usage_records")
    suspend fun getAll(): List<MTUsageData>

    @Insert
    suspend fun insert(usageRecord: MTUsageData)

    @Update
    suspend fun updateByDay(vararg usageRecord: MTUsageData)
}