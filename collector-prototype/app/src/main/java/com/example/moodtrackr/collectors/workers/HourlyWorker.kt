package com.example.moodtrackr.collectors.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.util.DatesUtil
import java.util.*

class HourlyWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private var context: Context = context.applicationContext

    override fun doWork(): Result {
        // Update Notification
        NotifUpdateUtil.updateNotif(context.applicationContext)

        //Run daily collection sequence for yesterday
        CollectionUtil.dailyCollectYesterday(context)
        val tokenExpiry = DataCollectorService.tokenExpiry
        if (tokenExpiry !== null && tokenExpiry < Date().time - 2*60*60*1000) {
            Auth0Manager.refreshCredentials(context)
            DataCollectorService.tokenExpiry = Date().time
        }
        return Result.success()
    }
}