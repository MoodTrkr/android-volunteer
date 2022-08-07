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
        val TITLE: String = "MDTKR"
        val NOTIF_ID: Int = 1005

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
            val builder = NotificationCompat.Builder(context, NOTIF_ID.toString())
                .setContentTitle(TITLE)
                .setTicker(TITLE)
                .setContentText("Survey for yesterday now open!")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(createNotifContext(context))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
            createChannel(notificationManager)
            notificationManager.notify(NOTIF_ID, builder.build())

        }

        private fun createChannel(notificationManager: NotificationManager) {
            val descriptionText = "Used by Mood Tracker"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(NOTIF_ID.toString(), TITLE, importance)
            mChannel.description = descriptionText
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}