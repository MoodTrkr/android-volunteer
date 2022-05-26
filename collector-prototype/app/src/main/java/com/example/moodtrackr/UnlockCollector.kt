package com.example.moodtrackr

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room

class UnlockCollector (context: FragmentActivity?){

    private var appContext: Context
    private var db :AppDatabase

    init {
        appContext = context!!.applicationContext
        db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "app-database"
        ).build()
    }

    fun getUnlockCount24h(): Int {
        val unlockRecordDAO = db.unlockRecordDAO()

        return unlockRecordDAO.getAllInTimeRange(System.currentTimeMillis()-86400000, System.currentTimeMillis()).count()
    }
}