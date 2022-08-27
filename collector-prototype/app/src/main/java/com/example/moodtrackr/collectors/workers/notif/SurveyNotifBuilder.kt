package com.example.moodtrackr.collectors.workers.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.moodtrackr.MainActivity
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.service.DataCollectorService

class SurveyNotifBuilder {
    companion object {
        private fun createNotifContext(context: Context): PendingIntent {
            val bundle = Bundle()
            bundle.putBoolean(MainActivity.SURVEY_NOTIF_CLICKED, true)
            return NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.surveyFragment)
                .setArguments(bundle)
                .createPendingIntent()
        }

        fun buildNotif(context: Context) {
            val notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as
                    NotificationManager
            val builder = NotificationCompat.Builder(context, MainActivity.SURVEY_NOTIF_ID.toString())
                .setContentTitle(MainActivity.TITLE)
                .setTicker(MainActivity.TITLE)
                .setContentText("Survey for yesterday now open!")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(createNotifContext(context))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
            createChannel(notificationManager)
            notificationManager.notify(MainActivity.SURVEY_NOTIF_ID, builder.build())

        }

        private fun createChannel(notificationManager: NotificationManager) {
            val descriptionText = "Used by Mood Tracker"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(MainActivity.SURVEY_NOTIF_ID.toString(), MainActivity.TITLE, importance)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}