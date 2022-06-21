package com.example.moodtrackr.extractors

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import kotlinx.coroutines.runBlocking

class UnlockCollector (context: FragmentActivity?){

    private var appContext: Context
    private var db : AppDatabase

    init {
        appContext = context!!.applicationContext
        db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    fun getUnlockCount24h(): Int {
        val usageRecordDAO = db.rtUsageRecordsDAO

//        return runBlocking { usageRecordDAO.getAllUnlocksInTimeRange(System.currentTimeMillis()-86400000, System.currentTimeMillis()).count() }
        return 0
    }
}