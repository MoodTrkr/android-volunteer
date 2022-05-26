package com.example.moodtrackr

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.os.IBinder

class DataCollectorService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("DEBUG", "Hello World")

        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        val unlockReceiver = DeviceUnlockReceiver()
        registerReceiver(unlockReceiver,filter)

        if (intent?.action != null && intent.action.equals(
                "ACTION_STOP", ignoreCase = true)) {
            Log.e("DEBUG", "Service Stopped")
            stopSelf()
        }

        // If we get killed, after returning from here, restart
        return START_NOT_STICKY
//       change to start sticky later
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }
}