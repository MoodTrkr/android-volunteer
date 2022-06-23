package com.example.moodtrackr.collectors

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageDataDAO
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking
import java.util.*

class CollectionUtil(context: FragmentActivity?) {
    private var usageExtractor: AppUsageExtractor
    private var callLogsExtractor: CallLogsStatsExtractor
    private var appContext: Context
    private var baseContext: Context

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context.baseContext
        this.usageExtractor = AppUsageExtractor(context)
        this.callLogsExtractor = CallLogsStatsExtractor(context)
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

    companion object {
        private var usageRecordsDAO: UsageRecordsDAO = DatabaseManager.usageRecordsDAO
        private var rtUsageRecordsDAO: RTUsageDataDAO = DatabaseManager.rtUsageRecordsDAO

        fun periodicCollectToday(): Pair<Long, Long> {
            return periodicCollect(DatesUtil.getTodayTruncated())
        }

        fun periodicCollect(day: Date): Pair<Long, Long> {
            var pair: Pair<Long, Long>
            runBlocking {
                pair = Pair(
                    rtUsageRecordsDAO.getStepsOnDay(day.time),
                    rtUsageRecordsDAO.getUnlocksOnDay(day.time)
                )
            }
            return pair
        }
    }
}