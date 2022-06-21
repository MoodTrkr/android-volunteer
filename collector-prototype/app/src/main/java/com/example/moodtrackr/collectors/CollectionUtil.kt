package com.example.moodtrackr.collectors

import PersistentWorker
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.work.*
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.realtime.RTUsageRecordsDAO
import com.example.moodtrackr.db.records.UsageRecordsDAO
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit

class CollectionUtil(context: Context) {
    private var usageExtractor: AppUsageExtractor
    private var callLogsExtractor: CallLogsStatsExtractor
    private var appContext: Context
    private var baseContext: Context

    init {
        this.baseContext = context
        this.appContext = context.applicationContext
        this.usageExtractor = AppUsageExtractor(context)
        this.callLogsExtractor = CallLogsStatsExtractor(context)
    }

    constructor(activity: FragmentActivity?) : this(activity!!.applicationContext)

    fun dailyCollection() {

    }

    fun dbInit() {
        var usage = MTUsageData()
        runBlocking { usageRecordsDAO.insert(usage)}
    }

    fun getAll(): List<MTUsageData> {
        return runBlocking { usageRecordsDAO.getAll() }
    }

    fun queuePersistent() {
//        WorkManager
//            .getInstance(appContext)
//            .enqueue(buildPeristent())
        WorkManager
            .getInstance(appContext)
            .enqueueUniquePeriodicWork("MT_PERSISTENT_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildPeristent())
    }

    private fun buildPeristent(): PeriodicWorkRequest {
//        return OneTimeWorkRequestBuilder<PersistentWorker>()
//                // Additional configuration
//                .build()
        return PeriodicWorkRequestBuilder<PersistentWorker>(
            16,
            TimeUnit.MINUTES)
            .build()
    }

    companion object {
        private var usageRecordsDAO: UsageRecordsDAO = DatabaseManager.usageRecordsDAO
        private var rtUsageRecordsDAO: RTUsageRecordsDAO = DatabaseManager.rtUsageRecordsDAO

        fun periodicCollectToday(): Pair<Long, Long> {
            return periodicCollect(DatesUtil.getTodayTruncated())
        }

        fun periodicCollect(day: Date): Pair<Long, Long> {
            var pair: Pair<Long, Long>
            runBlocking {
                pair = Pair(
                    rtUsageRecordsDAO.getStepsOnDay(day.time),
                    rtUsageRecordsDAO.getUnlocksOnDay(day.time)
                ) as Pair<Long, Long>
            }
            return pair
        }
    }
}