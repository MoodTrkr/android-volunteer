package com.example.moodtrackr.collectors.workers.util

import android.content.Context
import androidx.work.*
import com.example.moodtrackr.collectors.service.util.NotifUpdateUtil
import com.example.moodtrackr.collectors.workers.*
import java.util.concurrent.TimeUnit

class WorkersUtil {
    companion object {
        fun queueAll(context: Context) {
            queueServiceMaintainenceOneTime(context)
            NotifUpdateUtil.updateNotif(context)

            queueServiceMaintenance(context)
            queuePeriodic(context)
            queueHourly(context)
            queueUpdatesServicePeriodic(context)
        }

        fun queueUpdatesServiceOneTime(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueue(buildUpdatesWorkerOneTime())
        }

        fun queueUpdatesServicePeriodic(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_UPDATES_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildUpdatesWorkerPeriodic())
        }

        fun queueServiceMaintainenceOneTime(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueue(buildServiceMaintainenceOneTime())
        }

        fun queueServiceMaintenance(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_SERVICE_MAINTENANCE_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildServiceMaintainence())
        }

        fun queuePeriodic(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_PERIODIC_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildPeriodic())
        }

        fun queueHourly(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_HOURLY_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildHourly())
        }

        fun queueDaily(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_DAILY_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildDaily())
        }

        fun queueRouterRequestsWorker(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork("MDTKR_ROUTER_REQUESTS_WORKER", ExistingPeriodicWorkPolicy.KEEP, buildRouterRequestsWorker())
        }

        fun buildUpdatesWorkerOneTime(): OneTimeWorkRequest {
            return OneTimeWorkRequest.Builder(UpdatesWorker::class.java).build()
        }

        fun buildUpdatesWorkerPeriodic(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<UpdatesWorker>(
                1,
            TimeUnit.DAYS)
                .build()
        }

        fun buildServiceMaintainenceOneTime(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ServiceMaintainenceWorker>()
                .build()
        }

        fun buildServiceMaintainence(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<ServiceMaintainenceWorker>(
                20,
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

        fun buildRouterRequestsWorker(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<RouterRequestsWorker>(
                1,
                TimeUnit.HOURS)
                .build()
        }
    }
}