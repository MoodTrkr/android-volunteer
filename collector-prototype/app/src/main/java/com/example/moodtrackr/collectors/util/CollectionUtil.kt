package com.example.moodtrackr.collectors.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.db.DBHelper
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.data.DailyCollection
import com.example.moodtrackr.data.PeriodicCollection
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.extractors.sleep.data.MTSleepData
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.extractors.usage.data.MTAppUsageLogs
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class CollectionUtil(context: Context) {
    private var appContext: Context
    private var baseContext: Context

    init {
        this.baseContext = context
        this.appContext = context.applicationContext
    }

    constructor(activity: FragmentActivity?) : this(activity!!.applicationContext)

    fun getAll(): List<MTUsageData> {
        return runBlocking { DatabaseManager.getInstance(appContext).usageRecordsDAO.getAll() }
    }

    companion object {
        fun periodicCollectToday(context: Context) {
            periodicCollect(context, DatesUtil.getToday())
        }

        fun periodicCollect(context: Context, day: Date) {
            val dayTruncated = DatesUtil.truncateDate(day)
            CoroutineScope(Dispatchers.IO).launch {
                var lastQueried: Long? = SharedPreferencesStorage(context)
                    .retrieveLong(context.resources.getString(R.string.mdtkr_prev_query))
                if (lastQueried==null) {
                    lastQueried = Date().time-20*60*1000
                }

                val usageStats: MTAppUsageStats = AppUsageExtractor(context.applicationContext)
                    .usageStatsQuery(lastQueried, Date().time)
                val rtRecord: RTUsageRecord = DBHelperRT.getObjSafe(context, dayTruncated)
                val record: MTUsageData = DBHelper.getObjSafe(context, dayTruncated)
                record.periodicCollBook.insert(
                    PeriodicCollection(Date().time,
                        usageStats,
                        StepsCountExtractor.stepsChange(rtRecord.steps),
                        rtRecord.unlocks,
                    )
                )
                DBHelper.updateDB(context, record)
            }
        }

        fun dailyCollectYesterday(context: Context) {
            dailyCollect(context, DatesUtil.getYesterday())
        }

        fun dailyCollectToday(context: Context) {
            dailyCollect(context, DatesUtil.getToday())
        }

        fun dailyCollect(context: Context, day: Date) {
            val dayTruncated = DatesUtil.truncateDate(day)
            val usageExtractor = AppUsageExtractor(context)
            val callLogsExtractor = CallLogsStatsExtractor(context)

            CoroutineScope(Dispatchers.IO).launch {
                val record: MTUsageData = DBHelper.getObjSafe(context, dayTruncated)
                if (!record.dailyCollection.complete) {
                    val timeBounds: Pair<Long, Long> = DatesUtil.getDayBounds(dayTruncated)
                    record.dailyCollection = DailyCollection(
                        dayTruncated.time,
                        usageExtractor.usageEventsQuery(timeBounds.first, timeBounds.second),
                        callLogsExtractor.queryLogs(timeBounds.first, timeBounds.second),
                        MTSleepData(0,0),
                        usageExtractor.screenOnTimeQuery(timeBounds.first, timeBounds.second),
                        true
                    )
                    DBHelper.updateDB(context, record)
                }
            }
        }
    }
}