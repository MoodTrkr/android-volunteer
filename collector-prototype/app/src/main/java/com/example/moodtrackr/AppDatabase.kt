package com.example.moodtrackr

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UnlockRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun unlockRecordDAO(): UnlockRecordDAO
}