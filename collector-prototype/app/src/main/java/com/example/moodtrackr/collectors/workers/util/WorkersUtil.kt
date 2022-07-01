package com.example.moodtrackr.collectors.workers.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moodtrackr.collectors.workers.DailyWorker
import com.example.moodtrackr.collectors.workers.HourlyWorker
import com.example.moodtrackr.collectors.workers.PeriodicWorker
import com.example.moodtrackr.collectors.workers.PersistentWorker
import java.util.concurrent.TimeUnit

class WorkersUtil {
    companion object {
        fun queuePersistent(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MT_PERSISTENT_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildPersistent())
        }

        fun queuePeriodic(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MT_PERIODIC_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildPeriodic())
        }

        fun queueHourly(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MT_HOURLY_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildHourly())
        }

        fun queueDaily(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MT_DAILY_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildDaily())
        }

        fun buildPersistent(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PersistentWorker>(
                60,
                TimeUnit.MINUTES)
                .build()
        }

        fun buildPeriodic(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<PeriodicWorker>(
                20,
                TimeUnit.MINUTES)
                .build()
        }

        fun buildHourly(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<HourlyWorker>(
                1,
                TimeUnit.HOURS)
                .build()
        }

        fun buildDaily(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<DailyWorker>(
                1,
                TimeUnit.DAYS)
                .build()
        }
    }
}