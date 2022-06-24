package com.example.moodtrackr.collectors.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.collectors.db.DBHelper
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.data.DailyCollection
import com.example.moodtrackr.data.HourlyCollection
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
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
            runBlocking {
                val rtRecord: RTUsageRecord = DBHelperRT.getObjSafe(context, dayTruncated)
                val record: MTUsageData = DBHelper.getObjSafe(context, dayTruncated)
                if (!record.hourlyCollBook.isFull()) {
                    record.hourlyCollBook.insert(
                        HourlyCollection(Date(), rtRecord.unlocks, rtRecord.steps)
                    )
                    DBHelper.updateDB(context, record)
                }
            }
        }

        fun dailyCollectToday(context: Context) {
            periodicCollect(context, DatesUtil.getToday())
        }

        fun dailyCollect(context: Context, day: Date) {
            val dayTruncated = DatesUtil.truncateDate(day)
            val usageExtractor = AppUsageExtractor(context)
            val callLogsExtractor = CallLogsStatsExtractor(context)
            runBlocking {
                val record: MTUsageData = DBHelper.getObjSafe(context, dayTruncated)
                val timeBounds: Pair<Long, Long> = DatesUtil.getDayBounds(dayTruncated)
                if (!record.dailyCollection.complete) {
                    record.dailyCollection = DailyCollection(
                        dayTruncated,
                        usageExtractor.usageStatsQuery(timeBounds.first, timeBounds.second),
                        usageExtractor.usageEventsQuery(timeBounds.first, timeBounds.second),
                        callLogsExtractor.queryLogs(timeBounds.first, timeBounds.second),
                        usageExtractor.screenOnTimeQuery(timeBounds.first, timeBounds.second),
                        true
                    )
                    DBHelper.updateDB(context, record)
                }
            }
        }
    }
}