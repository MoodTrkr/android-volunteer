package com.example.moodtrackr.db.records

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.sql.Date

//@Dao
@Deprecated("Do not use")
interface UsageDataDAO {

//    @Query("SELECT u.usage_data FROM usageRecords u WHERE u.date >= :start AND u.date <= :end")
    fun getByTimeRange(start: Long, end: Long): List<String>

//    @Query("SELECT strftime('%d', :date) FROM usageRecords")
    fun getByDate(date: Date): List<String>

//    @Query("SELECT * FROM usageRecords")
    fun getAll(date: Date): List<String>

//    @Insert
    fun insert(usageRecord: UsageRecord)

//    @Update
    fun updateByDay(vararg usageRecord: UsageRecord)
}