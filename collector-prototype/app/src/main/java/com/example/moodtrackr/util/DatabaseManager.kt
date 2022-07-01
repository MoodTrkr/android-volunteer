package com.example.moodtrackr.util

import android.content.Context
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageRecordsDAO
import com.example.moodtrackr.db.records.UsageRecordsDAO

class DatabaseManager() {
    companion object {
        @Volatile private var db: AppDatabase? = null
        lateinit var usageRecordsDAO : UsageRecordsDAO
        lateinit var rtUsageRecordsDAO : RTUsageRecordsDAO

        fun getInstance(context: Context): AppDatabase {
            return db ?: synchronized(this) {
                db ?: build(context)!!.also { db = it }
            }
        }

        fun build(context: Context): AppDatabase? {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app-database"
            ).fallbackToDestructiveMigration()
            .build()
            usageRecordsDAO = db!!.usageRecordsDAO
            rtUsageRecordsDAO = db!!.rtUsageRecordsDAO
            return db
        }
    }
}