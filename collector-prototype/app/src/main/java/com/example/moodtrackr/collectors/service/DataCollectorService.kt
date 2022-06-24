package com.example.moodtrackr.collectors.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.extractors.unlocks.UnlockReceiver
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.runBlocking

class DataCollectorService : Service() {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private lateinit var stepsCounter: StepsCountExtractor
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        this.context = this.applicationContext
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as
                NotificationManager
        this.stepsCounter = StepsCountExtractor(this)

        runBlocking {
            val record: RTUsageRecord = DBHelperRT.getObjSafe(context, DatesUtil.getTodayTruncated())
            unlocks = record.unlocks
            steps = record.steps
        }

        builder = createNotif()
        createChannel()
        running = true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        val unlockReceiver = UnlockReceiver()
        registerReceiver(unlockReceiver, filter)

        if (intent?.action != null && intent.action.equals(
                "ACTION_STOP", ignoreCase = true)) {
            Log.e("DEBUG", "Service Stopped")
            stopSelf()
        }

        val notification = builder.build()

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
        val descriptionText = "Used by Mood Tracker"
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel(NOTIF_ID.toString(), TITLE, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }

    fun updateUnlocks() {
        builder.setContentText("Unlocks: $unlocks")
        notificationManager.notify(NOTIF_ID, builder.build())
    }

    private fun createNotif(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this.applicationContext, NOTIF_ID.toString())
            .setContentTitle(TITLE)
            .setTicker(TITLE)
            .setContentText("Unlocks: $unlocks | Steps: $steps")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
    }

    override fun onDestroy() {
        running = false
        this.stepsCounter.clean()
        stopForeground(true)
    }

    companion object {
        private val TITLE: String = "MDTKR"
        private val NOTIF_ID: Int = 0

        var unlocks: Long = 0
        var steps: Long = 0
        var running: Boolean = false
    }
}