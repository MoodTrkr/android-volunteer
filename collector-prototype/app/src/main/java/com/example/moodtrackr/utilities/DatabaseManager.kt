package com.example.moodtrackr.utilities

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageDataDAO
import com.example.moodtrackr.db.records.UsageRecordsDAO
import kotlin.concurrent.thread

class DatabaseManager(activity: FragmentActivity?) {
    private var appContext: Context = activity!!.applicationContext
    private var baseContext: Context = activity!!.baseContext

    init {
        db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "app-database"
        ).build()
        usageRecordsDAO = db.usageRecordsDAO()
        thread(start = true) {
            db.usageRecordsDAO()
        }
        rtUsageRecordsDAO = db.rtUsageDataDAO()
        thread(start = true) {
            db.rtUsageDataDAO()
        }
    }
    companion object {
        lateinit var db: AppDatabase
        lateinit var usageRecordsDAO : UsageRecordsDAO
        lateinit var rtUsageRecordsDAO : RTUsageDataDAO
    }
}