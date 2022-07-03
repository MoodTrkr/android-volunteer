package com.example.moodtrackr.collectors.workers.util

import android.content.Context
import androidx.work.*
import com.example.moodtrackr.collectors.workers.DailyWorker
import com.example.moodtrackr.collectors.workers.HourlyWorker
import com.example.moodtrackr.collectors.workers.PeriodicWorker
import com.example.moodtrackr.collectors.workers.ServiceMaintainenceWorker
import java.util.concurrent.TimeUnit

class WorkersUtil {
    companion object {
        fun queueServiceMaintainenceOneTime(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueue(buildServiceMaintainenceOneTime())
        }

        fun queueServiceMaintenance(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MT_SERVICE_MAINTENANCE_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildServiceMaintainence())
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

        fun buildServiceMaintainenceOneTime(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ServiceMaintainenceWorker>()
                .build()
        }

        fun buildServiceMaintainence(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<ServiceMaintainenceWorker>(
                60,
                TimeUnit.HOURS)
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