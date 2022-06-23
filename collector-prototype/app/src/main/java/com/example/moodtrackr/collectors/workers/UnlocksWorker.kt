package com.example.moodtrackr.collectors.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.utilities.DatabaseManager
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking

class UnlocksWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var context: Context = context

    override fun doWork(): Result {
        Log.e("DEBUG", "Test")
        runBlocking {
            val time = DatesUtil.getTodayTruncated().time
            var record: RTUsageRecord? = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(time)
            record = checkSequence(record)
            updateDBUnchecked(record!!.unlocks+1)
        }
        return Result.success()
    }

    private fun updateDBUnchecked(unlocks: Long) {
        runBlocking {
            var record = DatabaseManager.getInstance(context).rtUsageRecordsDAO.getObjOnDay(
                DatesUtil.getTodayTruncated().time
            )
            record!!.unlocks = unlocks
            DatabaseManager.getInstance(context).rtUsageRecordsDAO.update( record )
        }
    }

    private fun checkSequence(unlocksDB: RTUsageRecord?): RTUsageRecord {
        var unlocksDBNew : RTUsageRecord
        runBlocking {
            if (unlocksDB === null) {
                unlocksDBNew = RTUsageRecord(
                    DatesUtil.getTodayTruncated(),
                    0,
                    0
                )
                DatabaseManager.getInstance(context).rtUsageRecordsDAO.insertAll(unlocksDBNew)
            }
            else {
                unlocksDBNew = unlocksDB
            }
        }
        return unlocksDBNew
    }
}