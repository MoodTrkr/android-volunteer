package com.example.moodtrackr.collectors

import android.content.Context
import androidx.work.WorkerParameters
import androidx.work.Worker

class PersistentWorker(appContext: Context, params: WorkerParameters):
    Worker(appContext, params) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}