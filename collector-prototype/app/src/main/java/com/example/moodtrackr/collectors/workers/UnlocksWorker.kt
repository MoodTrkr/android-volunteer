package com.example.moodtrackr.collectors.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.runBlocking

class UnlocksWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var context: Context = context

    override fun doWork(): Result {
        Log.e("DEBUG", "Test")
        runBlocking {
            val time = DatesUtil.getTodayTruncated()
            var record: RTUsageRecord? = DBHelperRT.getObjSafe(context, time)
            DBHelperRT.updateDB(context, record!!.unlocks+1, StepsCountExtractor.stepsChange(record.steps))
            DataCollectorService.localUnlocks = record.unlocks+1
            DataCollectorService.localSteps = (StepsCountExtractor.steps- StepsCountExtractor.stepsLastUpdate + record.steps).toLong()
        }
        NotifUpdateUtil.updateNotif(this.applicationContext)
        return Result.success()
    }
}