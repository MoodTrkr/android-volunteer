package com.example.moodtrackr.collectors.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.moodtrackr.collectors.workers.util.WorkersUtil


class RestartReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = WorkManager.getInstance(context)
        WorkersUtil.queueServiceMaintainenceOneTime(context.applicationContext)
    }
}