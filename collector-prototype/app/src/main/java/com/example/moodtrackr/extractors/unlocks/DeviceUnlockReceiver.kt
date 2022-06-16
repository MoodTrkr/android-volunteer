package com.example.moodtrackr.extractors.unlocks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.db.records.UsageRecord
import java.util.*
import kotlin.concurrent.thread

class DeviceUnlockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app-database"

        ).build()

        val time = RTUsageRecord(Date(), "unlock", "1")
        Log.e("DEBUG", "Zeus")
        // Use an injected singleton db context for real implementation.

        thread(start = true) {
            db.rtUsageDataDAO().insertAll(time)
        }


    }
}