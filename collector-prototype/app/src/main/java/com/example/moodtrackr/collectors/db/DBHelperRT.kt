package com.example.moodtrackr.collectors.db

import android.content.Context
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
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
                return checkSequence(context, record)
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
            record = checkSequence(context, record)

            record!!.unlocks = unlocks
            record!!.steps = steps
            runBlocking { DatabaseManager.getInstance(context).rtUsageRecordsDAO.update(record!!) }
        }

        fun checkSequence(context: Context, record: RTUsageRecord?): RTUsageRecord {
            var recordNew : RTUsageRecord
            runBlocking {
                if (record === null) {
                    recordNew = RTUsageRecord(
                        DatesUtil.getTodayTruncated(),
                        0,
                        0
                    )
                    DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(recordNew)
                }
                else {
                    recordNew = record
                }
            }
            return recordNew
        }
    }
}