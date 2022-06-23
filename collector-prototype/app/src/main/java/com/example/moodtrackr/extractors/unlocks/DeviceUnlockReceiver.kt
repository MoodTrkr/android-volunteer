package com.example.moodtrackr.extractors.unlocks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import com.example.moodtrackr.db.realtime.RTUsageRecord
import com.example.moodtrackr.db.records.UsageRecord
import com.example.moodtrackr.utilities.DatesUtil
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.thread

class DeviceUnlockReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app-database"

        ).build()

//        val time = RTUsageRecord(DatesUtil.getToday(), "unlock", "1")
        val time = DatesUtil.getToday().time
        runBlocking {
            var unlocks: RTUsageRecord = db.rtUsageDataDAO().getUnlockObjOnDay(time)
            unlocks.usageVal+=1
            db.rtUsageDataDAO().update(unlocks)
        }
//        var unlocks: RTUsageRecord =
        Log.e("DEBUG", "Zeus")
        // Use an injected singleton db context for real implementation.


    }
}