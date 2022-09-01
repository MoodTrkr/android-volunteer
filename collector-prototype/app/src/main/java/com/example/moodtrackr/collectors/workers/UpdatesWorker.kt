package com.example.moodtrackr.collectors.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.util.UpdateManager

class UpdatesWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private var context: Context = context.applicationContext

    override fun doWork(): Result {
        UpdateManager.checkForUpdates(context.applicationContext)
        return Result.success()
    }
}