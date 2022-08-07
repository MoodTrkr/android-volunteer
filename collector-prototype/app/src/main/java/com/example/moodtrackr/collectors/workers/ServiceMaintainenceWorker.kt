package com.example.moodtrackr.collectors.workers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.moodtrackr.collectors.service.DataCollectorService


class ServiceMaintainenceWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var context: Context = context
    override fun doWork(): Result {
        Log.e("DEBUG", "doWork called for: " + this.id)
        Log.e("DEBUG", "Service Running: " + DataCollectorService.running)
        if (!DataCollectorService.running) {
            Log.d("DEBUG", "starting service from doWork")
            val intent = Intent(context.applicationContext, DataCollectorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }
}