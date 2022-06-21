package com.example.moodtrackr.extractors.unlocks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moodtrackr.R

class DataCollectorService : Service() {
    override fun onCreate() {
        super.onCreate()
        createChannel()
        running = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val id = 0
        val title = "MDTKR"
        Log.e("DEBUG", "Hello World")

        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        val unlockReceiver = DeviceUnlockReceiver()
        registerReceiver(unlockReceiver, filter)

        if (intent?.action != null && intent.action.equals(
                "ACTION_STOP", ignoreCase = true)) {
            Log.e("DEBUG", "Service Stopped")
            stopSelf()
        }

        val notification = NotificationCompat.Builder(applicationContext, id.toString())
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("progress")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .build()

        // If we get killed, after returning from here, restart
        startForeground(1, notification);
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    private fun createChannel() {
        // Create the NotificationChannel
        val name = "MDTKR"
        val descriptionText = "Used by Mood Tracker"
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel("0", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onDestroy() {
        running = false
        stopForeground(true)
    }

    companion object {
        var running: Boolean = false
    }
}