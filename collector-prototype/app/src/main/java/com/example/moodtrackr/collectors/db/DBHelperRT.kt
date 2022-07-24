package com.example.moodtrackr.collectors.db

import android.content.Context
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class DBHelperRT {
    companion object {
        fun getStepsSafe(context: Context, day: Date): Long {
            return getObjSafe(context, day).steps
        }

        fun getUnlocksSafe(context: Context, day: Date): Long {
            return getObjSafe(context, day).unlocks
        }

        fun getObjSafe(context: Context, day: Date): RTUsageRecord {
            val record: RTUsageRecord?
            runBlocking {
                record = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(day.time)
            }
            if (record == null) {
                return handleNull(context, record)
            }
            return record
        }

        fun updateDB(context: Context, unlocks: Long, steps: Long) {
            var record: RTUsageRecord?
            runBlocking {
                record = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(
                    DatesUtil.getTodayTruncated().time
                )
            }
            record = handleNull(context, record)

            record!!.unlocks = unlocks
            record!!.steps = steps
            CoroutineScope(Dispatchers.IO).launch { DatabaseManager.getInstance(context).rtUsageRecordsDAO.update(record!!) }

            DataCollectorService.localSteps = steps
            DataCollectorService.localUnlocks = unlocks
        }

        fun handleNull(context: Context, record: RTUsageRecord?): RTUsageRecord {
            var recordNew : RTUsageRecord
            runBlocking {
                if (record === null) {
                    recordNew = RTUsageRecord(
                        DatesUtil.getTodayTruncated(),
                        0,
                        0
                    )
                    DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(recordNew)
                    refreshStaticVars()
                }
                else {
                    recordNew = record
                }
            }
            return recordNew
        }

        private fun refreshStaticVars() {
            StepsCountExtractor.steps = 0
            StepsCountExtractor.stepsDBLastUpdate = 0
            DataCollectorService.localSteps = 0
            DataCollectorService.localUnlocks = 0
        }
    }
}