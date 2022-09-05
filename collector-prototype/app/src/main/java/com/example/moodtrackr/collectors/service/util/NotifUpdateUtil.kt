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
            val builder = NotificationCompat.Builder(context, MainActivity.PRIMARY_SERVICE_NOTIF_ID.toString())
                .setContentTitle(MainActivity.TITLE)
                .setTicker(MainActivity.TITLE)
                .setContentText("Unlocks: ${DataCollectorService.localUnlocks} | Steps: ${DataCollectorService.localSteps}")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setOngoing(true)
                .setContentIntent(createNotifContext(context))
                .setOnlyAlertOnce(true)
            createChannel(notificationManager)
            notificationManager.notify(MainActivity.PRIMARY_SERVICE_NOTIF_ID, builder.build())
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
            val mChannel = NotificationChannel(MainActivity.PRIMARY_SERVICE_NOTIF_ID.toString(), MainActivity.TITLE, importance)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}