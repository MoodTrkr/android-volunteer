package com.example.moodtrackr.extractors.unlocks

import UnlocksWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


class DeviceUnlockReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("DEBUG", "Lock/Unlocked")
        val manager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequest.Builder(UnlocksWorker::class.java).build()
        manager.enqueue(request)
    }
}