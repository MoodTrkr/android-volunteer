package com.example.moodtrackr.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.BuildConfig.VERSION_NAME
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.workers.UpdateDownloadWorker
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.AppUpdaterUtils.UpdateListener
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipException
import java.util.zip.ZipFile


class UpdateManager {
    companion object {
        /**
         *  Run this to check for git updates. Add your own function through the callback param.
         * */
        fun checkForGitUpdates(context: Context, callback: (context: Context, update: Update) -> Unit) {
            AppUpdaterUtils(context)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("MoodTrkr", "volunteer-app-release")
                .withListener(object : UpdateListener {
                    override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                        callback.invoke(context, update)
                    }
                    override fun onFailed(error: AppUpdaterError) {
                        Log.e("AppUpdater Error", "Something went wrong")
                        Log.e("AppUpdater Error", error.toString())
                    }
                })
            .start()
        }

        fun checkForUpdates(context: Context) {
            checkForGitUpdates(context, this::downloadUpdatesCallback)
        }

        private fun downloadUpdatesCallback(context: Context, update: Update) {
            if (update.latestVersion == getPackageVersion()) {
                Log.e("MDTKR_UPDATE","Application on Latest Version: ${getPackageVersion()}")
                return
            }
            val updateDownloadStatus = SharedPreferencesStorage(context).retrieveBoolean(context.resources.getString(R.string.mdtkr_update_downloaded))

            Log.e("MDTKR_UPDATE","Update Available: ${update}")
            if (update.latestVersion == getPackageVersion() && updateDownloadStatus == true) {
                handleUpdated(context)
                return
            }
            if (update.latestVersion != null) Log.e("Latest Version", update.latestVersion)
            if (update.urlToDownload != null) Log.e("URL", update.urlToDownload.toString())
            if (update.latestVersion != null && update.urlToDownload != null) {
                val myFile = File(context.filesDir, "updates/${update.latestVersion}.zip")
                if (myFile.exists()) myFile.delete()
                if (!myFile.parentFile.exists()) myFile.parentFile.mkdirs()
                myFile.createNewFile()

                val data = Data.Builder()
                var url =
                    "MoodTrkr/volunteer-app-release/archive/refs/tags/${update.latestVersion}.zip"
                data.putString(UpdateDownloadWorker.KEY_INPUT_URL, url)
                data.putString(UpdateDownloadWorker.KEY_OUTPUT_FILE_NAME, myFile.absolutePath)
                val downloadWorker = OneTimeWorkRequest.Builder(UpdateDownloadWorker::class.java)
                    .setInputData(data.build())
                WorkManager
                    .getInstance(context)
                    .enqueue(downloadWorker.build())
            }
        }

        /**
         * https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae
         * */
        fun unzipFile(path:String, destDirectory:String) : Int {
            File(destDirectory).run {
                if (!exists()) {
                    mkdirs()
                }
            }
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
                Log.e("MDTKR_ZIP", "I think somethn worked")
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

        /**
         *  Runs upon every application launch to check whether updates have been downloaded.
         *  Installs Downloaded Updates!
         * */
        fun checkUpdatesDownloaded(context: Context) {
            val updateDownloadStatus = SharedPreferencesStorage(context).retrieveBoolean(context.resources.getString(R.string.mdtkr_update_downloaded))
            val updateDownloadPath = SharedPreferencesStorage(context).retrieveString(context.resources.getString(R.string.mdtkr_update_loc))
            if (updateDownloadPath == null || updateDownloadStatus == null) return
            if (updateDownloadStatus!=true) {
                handleUpdated(context)
                return
            }

            val intent = Intent(Intent.ACTION_VIEW)
            val updateFile = File("$updateDownloadPath/volunteer-app-release/main.apk")
            intent.setDataAndType( FileProvider.getUriForFile(context, context.packageName+".provider", updateFile), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }

        fun getPackageVersion(): String {
            return VERSION_NAME
        }

        /**
         *  Run this whenever updated. Deletes update files from app directory.
         * */
        fun handleUpdated(context: Context) {
            val updateFolder = File(context.filesDir, "updates/")
            if (updateFolder.exists()) updateFolder.delete()
            SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_update_downloaded), false)
        }
    }
}
