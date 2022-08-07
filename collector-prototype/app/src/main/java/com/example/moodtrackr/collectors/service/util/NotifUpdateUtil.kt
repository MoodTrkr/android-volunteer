package com.example.moodtrackr.collectors.service.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavDeepLinkBuilder
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.service.DataCollectorService
import com.example.moodtrackr.collectors.workers.notif.SurveyNotifBuilder

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
                .setContentIntent(createNotifContext(context))
                .setOnlyAlertOnce(true)
            createChannel(notificationManager)
            notificationManager.notify(DataCollectorService.NOTIF_ID, builder.build())
        }

        private fun createNotifContext(context: Context): PendingIntent {
            return NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.surveyFragment)
                .createPendingIntent()
        }

        private fun createChannel(notificationManager: NotificationManager) {
            val descriptionText = "Used by Mood Tracker"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(DataCollectorService.NOTIF_ID.toString(), DataCollectorService.TITLE, importance)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}