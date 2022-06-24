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


class PersistentWorker(context: Context, parameters: WorkerParameters) :
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
            val intent = Intent(context, DataCollectorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
        return Result.success()
    }

    private fun setupExtractors() {
        // Downloads a file and updates bytes read
        // Calls setForegroundInfo() periodically when it needs to update
        // the ongoing Notification
//        DeviceUnlockReceiver(context)
        StepsCountExtractor(context!!)
    }

    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = 0
        val title = "MDTKR"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id.toString())
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .build()

        return ForegroundInfo(0, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create the NotificationChannel
        val name = "MDTKR"
        val descriptionText = "Used by Mood Tracker"
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel("0", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}