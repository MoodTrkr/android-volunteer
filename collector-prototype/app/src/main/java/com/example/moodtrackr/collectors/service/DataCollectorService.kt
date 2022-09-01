package com.example.moodtrackr.collectors.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.db.DBHelperRT
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.extractors.steps.StepsCountExtractor
import com.example.moodtrackr.extractors.unlocks.UnlockReceiver
import com.example.moodtrackr.util.DatesUtil

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
        Log.d("DataCollectorService", "DataCollectorService onCreate Triggered!")

        builder = createNotif(getState())
        createChannel()
        notification = builder.build()
        startForeground(MainActivity.PRIMARY_SERVICE_NOTIF_ID, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON) //formerly ACTION_USER_PRESENT
        Log.d("DataCollectorService", "DataCollectorService onStartCommand Triggered!")

        this.stepsCounter = StepsCountExtractor(this)
        unlockReceiver = UnlockReceiver()
        registerReceiver(unlockReceiver, filter)

        if (intent?.action != null && intent.action.equals(
                "ACTION_STOP", ignoreCase = true)) {
            Log.d("DEBUG", "Service Stopped")
            stopSelf()
        }

        // If we get killed, after returning from here, restart
        builder = createNotif(getState())
        createChannel()
        notification = builder.build()
        startForeground(MainActivity.PRIMARY_SERVICE_NOTIF_ID, notification)
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
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(MainActivity.PRIMARY_SERVICE_NOTIF_ID.toString(), MainActivity.TITLE, importance)
        mChannel.description = descriptionText
        notificationManager.createNotificationChannel(mChannel)
    }

    private fun createNotifContext(): PendingIntent {
        return NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.surveyFragment)
            .createPendingIntent()
    }

    private fun createNotif(state: Pair<Long, Long>): NotificationCompat.Builder {
        Log.d("DataCollectorService", "Notification State: $state")
        return NotificationCompat.Builder(this.applicationContext, MainActivity.PRIMARY_SERVICE_NOTIF_ID.toString())
            .setContentTitle(MainActivity.TITLE)
            .setTicker(MainActivity.TITLE)
            .setContentText("Unlocks: ${state.first} | Steps: ${state.second}")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(createNotifContext())
    }

    private fun getState(): Pair<Long, Long> {
        tokenExpiry = SharedPreferencesStorage(context)
            .retrieveLong(context.resources.getString(R.string.token_expiry))

        val record: RTUsageRecord = DBHelperRT.getObjSafe(context, DatesUtil.getTodayTruncated())
        localUnlocks = record.unlocks
        localSteps = record.steps
        Log.d("DataCollectorService", "DataCollectorService getState: ${record}")
        return Pair(localUnlocks, localSteps)
    }

    override fun onDestroy() {
        running = false
        if(this::stepsCounter.isInitialized) this.stepsCounter.clean()
        if(this::unlockReceiver.isInitialized) unregisterReceiver(unlockReceiver)
        stopForeground(true)
        notificationManager.cancel(MainActivity.PRIMARY_SERVICE_NOTIF_ID);
    }

    companion object {
        var tokenExpiry: Long? = null

        var localSteps: Long = 0 //meant to be updated by StepsCountExtractor
        var localUnlocks: Long = 0 //meant to be updated by UnlocksReceiver
        var running: Boolean = false
    }
}