package com.example.moodtrackr.collectors.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.util.DatesUtil
import java.util.*

class PeriodicWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private var context: Context = context.applicationContext

    override fun doWork(): Result {
        // Mark the Worker as important
        CollectionUtil.periodicCollectToday(context)

        if ( DatesUtil.getTomorrow().time - Date().time < 1200000 ) { CollectionUtil.dailyCollectToday(context) }
        return Result.success()
    }
}