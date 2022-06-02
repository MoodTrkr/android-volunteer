package com.example.moodtrackr.utilities

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.moodtrackr.db.AppDatabase
import kotlin.concurrent.thread

class DatabaseManager(activity: FragmentActivity?) {
    private var appContext: Context = activity!!.applicationContext
    private var baseContext: Context = activity!!.baseContext
    lateinit var db: AppDatabase

    init {
        this.db = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "app-database"
        ).build()

        thread(start = true) {
            db.usageDataDAO()
        }
    }
}