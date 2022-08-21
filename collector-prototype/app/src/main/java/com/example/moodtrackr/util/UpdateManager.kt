package com.example.moodtrackr.util

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.moodtrackr.collectors.workers.DownloadWorker
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import java.io.File
import java.util.*


class UpdateManager {
    companion object {
        fun checkForUpdates(context: Context) {
            val appUpdaterUtils = AppUpdaterUtils(context) //.setUpdateFrom(UpdateFrom.AMAZON)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("MoodTrkr", "volunteer-app-release")
                .withListener(object : UpdateListener {
                    override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                        Log.d("Update", update.toString())
                        if (update.latestVersion != null) Log.d("Latest Version", update.latestVersion)
                        if (update.urlToDownload != null) Log.d("URL", update.urlToDownload.toString())
                        if (update.latestVersion != null && update.urlToDownload != null) {
                            val myFile = File(context.filesDir, "updates/${update.latestVersion}.zip")
                            if (myFile.exists())                            myFile.delete()
                            if (!myFile.parentFile.exists())                myFile.parentFile.mkdirs()
                            myFile.createNewFile()

                            val data = Data.Builder()
                            var url = "MoodTrkr/volunteer-app-release/archive/refs/tags/${update.latestVersion}.zip"
                            data.putString(DownloadWorker.KEY_INPUT_URL, url)
                            data.putString(DownloadWorker.KEY_OUTPUT_FILE_NAME, myFile.absolutePath)
                            val downloadWorker = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                                .setInputData(data.build())
                            WorkManager
                                .getInstance(context)
                                .enqueue(downloadWorker.build())
                        }
                    }

                    override fun onFailed(error: AppUpdaterError) {
                        Log.d("AppUpdater Error", "Something went wrong")
                        Log.d("AppUpdater Error", error.toString())
                    }
                })
            appUpdaterUtils.start()
        }
    }
}
