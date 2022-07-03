package com.example.moodtrackr.collectors.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.extractors.unlocks.UnlockReceiver
import com.example.moodtrackr.util.DatesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataCollectorService : Service() {
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private lateinit var stepsCounter: StepsCountExtractor
    private lateinit var unlockReceiver: UnlockReceiver
    private lateinit var context: Context
    private lateinit var notification: Notification

    override fun onCreate() {
        super.onCreate()
        this.context = this.applicationContext
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as
                NotificationManager
        Log.e("DataCollectorService", "DataCollectorService onCreate Triggered!")

        builder = createNotif(getState())
        createChannel()
        notification = builder.build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        Log.e("DataCollectorService", "DataCollectorService onStartCommand Triggered!")

        this.stepsCounter = StepsCountExtractor(this)
        unlockReceiver = UnlockReceiver()
        registerReceiver(unlockReceiver, filter)

        if (intent?.action != null && intent.action.equals(
                "ACTION_STOP", ignoreCase = true)) {
            Log.e("DEBUG", "Service Stopped")
            stopSelf()
        }

        // If we get killed, after returning from here, restart
        startForeground(NOTIF_ID, notification);
        running = true
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    private fun createChannel() {
        // Create the NotificationChannel
        val descriptionText = "Used by Mood Tracker"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(NOTIF_ID.toString(), TITLE, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createNotif(state: Pair<Long, Long>): NotificationCompat.Builder {
        Log.e("DataCollectorService", "Notification State: $state")
        return NotificationCompat.Builder(this.applicationContext, NOTIF_ID.toString())
            .setContentTitle(TITLE)
            .setTicker(TITLE)
            .setContentText("Unlocks: ${state.first} | Steps: ${state.second}")
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
    }

    private fun getState(): Pair<Long, Long> {
        val record: RTUsageRecord = DBHelperRT.getObjSafe(context, DatesUtil.getTodayTruncated())
        unlocks = record.unlocks
        steps = record.steps
        Log.e("DataCollectorService", "DataCollectorService getState: ${record}")
        return Pair(unlocks, steps)
    }

    private fun saveState() {
        CoroutineScope(Dispatchers.IO).launch {
            DBHelperRT.updateDB(applicationContext, unlocks, steps)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        saveState()
    }

    override fun onTrimMemory(level: Int) {
        saveState()
    }

    override fun onDestroy() {
        running = false
        saveState()
        this.stepsCounter.clean()
        unregisterReceiver(unlockReceiver)
        stopForeground(true)
    }

    companion object {
        val TITLE: String = "MDTKR"
        val NOTIF_ID: Int = 0

        var steps: Long = 0 //meant to just follow StepsCountExtractor.steps
        var unlocks: Long = 0
        var running: Boolean = false
    }
}