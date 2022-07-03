package com.example.moodtrackr.collectors.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.moodtrackr.R
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.collectors.service.DataCollectorService


class ServiceMaintainenceWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    private var context: Context = context
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

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