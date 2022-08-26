package com.example.moodtrackr.collectors.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.router.RestClient
import kotlinx.coroutines.Dispatchers

class UpdateDownloadWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {
    private var context = context
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(KEY_INPUT_URL)
            ?: return Result.failure()
        val outputFile = inputData.getString(KEY_OUTPUT_FILE_NAME)
            ?: return Result.failure()
        // Mark the Worker as important
        val desc = "Starting Download"
        setForeground(createForegroundInfo(desc))
        Log.e("MDTKR_REST", "DOWNLOADING, $inputUrl, $outputFile")
        download(inputUrl, outputFile)
        return Result.success()
    }

    private suspend fun download(url: String, path: String) {
        // Downloads a file and updates bytes read
        // Calls setForegroundInfo() periodically when it needs to update
        // the ongoing Notification
        RestClient.safeUpdateDownload(this, context, Dispatchers.IO, url, path)
    }
    // Creates an instance of ForegroundInfo which can be used to update the
    // ongoing notification.
    private fun createForegroundInfo(text: String): ForegroundInfo {
        val id = MainActivity.DOWNLOAD_SERVICE_NOTIF_ID
        val title = MainActivity.TITLE
        val cancel = "Cancel Download"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        builder = NotificationCompat.Builder(applicationContext, "$id")
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)

        return ForegroundInfo(MainActivity.DOWNLOAD_SERVICE_NOTIF_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create the NotificationChannel
        val descriptionText = "Downloading an Update!"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(MainActivity.DOWNLOAD_SERVICE_NOTIF_ID.toString(), MainActivity.TITLE, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }

    fun updateProgress(downloaded: Int) {
        builder.setContentText("Downloaded: ${downloaded}MB")
        notificationManager.notify(MainActivity.DOWNLOAD_SERVICE_NOTIF_ID, builder.build())
    }

    fun cancelNotification() {
        notificationManager.cancel(MainActivity.DOWNLOAD_SERVICE_NOTIF_ID)
    }

    fun completeNotification() {
        builder.setContentText("Completed Download!")
        notificationManager.notify(MainActivity.DOWNLOAD_SERVICE_NOTIF_ID, builder.build())
    }

    companion object {
        const val KEY_INPUT_URL = "KEY_INPUT_URL"
        const val KEY_OUTPUT_FILE_NAME = "KEY_OUTPUT_FILE_NAME"
    }
}