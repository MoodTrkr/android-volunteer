package com.example.moodtrackr.collectors.db

import android.content.Context
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.runBlocking
import java.util.*

class DBHelper {
    companion object {
        fun getObjSafe(context: Context, day: Date): MTUsageData {
            val record: MTUsageData?
            runBlocking {
                record = DatabaseManager.getInstance(context).usageRecordsDAO.getObjOnDay(day)
            }
            if (record == null) {
                return checkSequence(context, record, MTUsageData(day))
            }
            return record
        }

        fun updateDB(context: Context, usageData: MTUsageData) {
            var record: MTUsageData?
            runBlocking {
                record = DatabaseManager.getInstance(context).usageRecordsDAO.getObjOnDay( usageData.date )
            }
            record = checkSequence(context, record, usageData)
            runBlocking { DatabaseManager.getInstance(context).usageRecordsDAO.update(usageData) }
        }

        fun checkSequence(context: Context, record: MTUsageData?, usageData: MTUsageData): MTUsageData {
            var recordNew : MTUsageData
            runBlocking {
                if (record === null) {
                    recordNew = usageData
                    DatabaseManager.getInstance(context).usageRecordsDAO.insert(recordNew)
                }
                else {
                    recordNew = record
                }
            }
            return recordNew
        }
    }
}