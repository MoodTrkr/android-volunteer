package com.example.moodtrackr.collectors.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.util.CollectionUtil
import com.example.moodtrackr.router.RestClient
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RouterRequestsWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    private var context: Context = context.applicationContext

    override fun doWork(): Result {
        RestClient.popRequest(context.applicationContext, Dispatchers.Default)
        return Result.success()
    }
}