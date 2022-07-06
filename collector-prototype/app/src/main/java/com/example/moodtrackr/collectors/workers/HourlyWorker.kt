package com.example.moodtrackr.collectors.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
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

        return Result.success()
    }
}