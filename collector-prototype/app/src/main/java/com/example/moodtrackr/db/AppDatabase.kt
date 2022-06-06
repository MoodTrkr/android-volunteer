package com.example.moodtrackr.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.moodtrackr.db.UsageData

@Database(entities = [UsageData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageDataDAO(): UsageDataDAO
}