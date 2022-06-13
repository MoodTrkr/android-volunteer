package com.example.moodtrackr.db

import DateConverter
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moodtrackr.db.realtime.RTUsageDataDAO
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.db.records.UsageDataDAO
import com.example.moodtrackr.db.records.UsageRecord

@Database(entities = [UsageRecord::class, RTUsageRecord::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageDataDAO(): UsageDataDAO
    abstract fun rtUsageDataDAO(): RTUsageDataDAO
}