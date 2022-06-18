package com.example.moodtrackr.db

import com.example.moodtrackr.db.utilities.DateConverter
import com.example.moodtrackr.db.utilities.MapConverter
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.utilities.*
import com.example.moodtrackr.db.realtime.RTUsageDataDAO
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.db.records.UsageRecordsDAO

@Database(entities = [MTUsageData::class, RTUsageRecord::class], version = 2)
@TypeConverters(DateConverter::class, MapConverter::class, StringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageRecordsDAO(): UsageRecordsDAO
    abstract fun rtUsageDataDAO(): RTUsageDataDAO
}