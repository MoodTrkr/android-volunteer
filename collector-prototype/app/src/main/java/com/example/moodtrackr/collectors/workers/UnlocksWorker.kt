package com.example.moodtrackr.collectors.workers

import android.content.Context
import android.util.Log
import androidx.work.*
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.workers.notif.SurveyNotifBuilder
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.runBlocking
import java.util.*

class UnlocksWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var context: Context = context

    override fun doWork(): Result {
        Log.e("DEBUG", "Test")
        unlocksUpdateSequence()
        surveyNotifSequence()
        return Result.success()
    }

    private fun unlocksUpdateSequence() {
        runBlocking {
            val time = DatesUtil.getTodayTruncated()
            var record: RTUsageRecord? = DBHelperRT.getObjSafe(context, time)
            DBHelperRT.updateDB(context, record!!.unlocks+1, StepsCountExtractor.stepsChange(record.steps))
            DataCollectorService.localUnlocks = record.unlocks+1
            DataCollectorService.localSteps = (StepsCountExtractor.steps - StepsCountExtractor.stepsLastUpdate + record.steps).toLong()
        }
        NotifUpdateUtil.updateNotif(this.applicationContext)
    }

    private fun surveyNotifSequence() {
        wasNotifSent = getNotifSentStatus(context) == true
        if (!wasNotifSent) {
            val hour = DatesUtil.getTodayCalendar().get(Calendar.HOUR_OF_DAY)
            if (hour>=4) {
                SurveyNotifBuilder.buildNotif(context)
            }
        }
    }

    companion object {
        var wasNotifSent: Boolean = true
        fun getNotifSentStatus(context: Context): Boolean? {
            return SharedPreferencesStorage(context)
                .retrieveBoolean(context.resources.getString(R.string.surveyNotifSent))
        }

        fun setNotifSentStatus(context: Context, value: Boolean) {
            return SharedPreferencesStorage(context)
                .store(context.resources.getString(R.string.surveyNotifSent), value)
        }
    }
}