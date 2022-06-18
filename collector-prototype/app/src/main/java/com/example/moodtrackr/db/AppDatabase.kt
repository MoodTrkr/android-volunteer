package com.example.moodtrackr.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsageRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageDataDAO(): UsageDataDAO
}