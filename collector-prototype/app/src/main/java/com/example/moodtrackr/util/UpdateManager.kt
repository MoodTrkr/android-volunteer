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
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.ZipException
import java.util.zip.ZipFile

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

        /**
         * https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
         * */
        fun unzipFile(path:String, destDirectory:String) : Int {

            try {
                ZipFile(path).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        zip.getInputStream(entry).use { input ->

                            val filePath = destDirectory + File.separator + entry.name

                            if (!entry.isDirectory) {
                                // if the entry is a file, extracts it
                                val bos = BufferedOutputStream(FileOutputStream(filePath))
                                val bytesIn = ByteArray(4096)
                                var read: Int
                                while (input.read(bytesIn).also { read = it } != -1) {
                                    bos.write(bytesIn, 0, read)
                                }
                                bos.close()

                            } else {
                                // if the entry is a directory, make the directory
                                val dir = File(filePath)
                                dir.mkdir()
                            }
                        }
                    }
                }
                return 0
            } catch (e: ZipException) {
                Log.e("ZipException", e.message.toString())
                Log.e("ZipException", "Zip Decode Error")
                return -1
            } catch (e: IOException) {
                Log.e("IOException", e.message.toString())
                Log.e("IOException", "File read error. Check if the file exists")
                return -1
            } catch (e: SecurityException) {
                Log.e("SecurityException", e.message.toString())
                Log.e("SecurityException", "Some random security bullshit happened")
                return -1
            }

        }
    }
}
