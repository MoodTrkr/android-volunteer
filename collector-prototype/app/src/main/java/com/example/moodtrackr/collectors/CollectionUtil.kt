package com.example.moodtrackr.collectors

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.utilities.DatabaseManager
import kotlinx.coroutines.runBlocking

class CollectionUtil(context: FragmentActivity?) {
    private var usageExtractor: AppUsageExtractor
    private var callLogsExtractor: CallLogsStatsExtractor
    private var appContext: Context
    private var baseContext: Context
    private var usageRecordsDAO: UsageRecordsDAO = DatabaseManager.usageRecordsDAO

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context.baseContext
        this.usageExtractor = AppUsageExtractor(context)
        this.callLogsExtractor = CallLogsStatsExtractor(context)
        this.usageRecordsDAO = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "app-database"

        ).build().usageRecordsDAO()
    }

    fun dailyCollection() {

    }

    fun dbInit() {
        var usage = MTUsageData()
        runBlocking { usageRecordsDAO.insert(usage)}
    }

    fun getAll(): List<MTUsageData> {
        return runBlocking { usageRecordsDAO.getAll() }
    }
}