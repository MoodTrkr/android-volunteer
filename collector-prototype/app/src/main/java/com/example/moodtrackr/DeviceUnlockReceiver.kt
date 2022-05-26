package com.example.moodtrackr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import kotlin.concurrent.thread

class DeviceUnlockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app-database"

        ).build()

        val time = UnlockRecord(System.currentTimeMillis())
        Log.e("DEBUG", "Zeus")
        // Use an injected singleton db context for real implementation.

        thread(start = true) {
            db.unlockRecordDAO().insertAll(time)
        }


    }
}