package com.example.moodtrackr.util

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageRecordsDAO
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.db.router.RouterRequestsDAO

class DatabaseManager() {
    companion object {
        @Volatile private var db: AppDatabase? = null
        lateinit var usageRecordsDAO : UsageRecordsDAO
        lateinit var rtUsageRecordsDAO : RTUsageRecordsDAO
        lateinit var routerRequestsDAO: RouterRequestsDAO

        fun getInstance(context: Context): AppDatabase {
            return db ?: synchronized(this) {
                db ?: build(context)!!.also { db = it }
            }
        }

        fun build(context: Context): AppDatabase? {
            db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "app-database"
            )
                .fallbackToDestructiveMigration()
//                .enableMultiInstanceInvalidation()
//                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .build()
            usageRecordsDAO = db!!.usageRecordsDAO
            rtUsageRecordsDAO = db!!.rtUsageRecordsDAO
            routerRequestsDAO = db!!.routerRequestsDAO
            return db
        }
    }
}