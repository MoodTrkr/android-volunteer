package com.example.moodtrackr.collectors.service.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.service.DataCollectorService

class NotifUpdateUtil {
    companion object {
        fun updateNotif(context: Context) {
            val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as
                    NotificationManager
            val builder = NotificationCompat.Builder(context, DataCollectorService.NOTIF_ID.toString())
                .setContentTitle(DataCollectorService.TITLE)
                .setTicker(DataCollectorService.TITLE)
                .setContentText("Unlocks: ${DataCollectorService.localUnlocks} | Steps: ${DataCollectorService.localSteps}")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
            createChannel(notificationManager)
            notificationManager.notify(DataCollectorService.NOTIF_ID, builder.build())
        }

        private fun createChannel(notificationManager: NotificationManager) {
            val descriptionText = "Used by Mood Tracker"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(DataCollectorService.NOTIF_ID.toString(), DataCollectorService.TITLE, importance)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}