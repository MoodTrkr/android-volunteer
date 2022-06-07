package com.example.moodtrackr.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsageDataDAO {

    @Query("SELECT u.usage_val FROM usageRecords u WHERE u.date >= :start AND u.date <= :end")
    fun getAllInTimeRange(start: Long, end: Long): List<UsageRecord>

    @Insert
    fun insertAll(vararg usageRecord: UsageRecord)

}