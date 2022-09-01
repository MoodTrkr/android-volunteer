package com.example.moodtrackr.util

import android.content.Context
import android.content.Intent
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
            if (!ConnectivityUtil.getMobileDataPreferences(context) &&
                ConnectivityUtil.isMobileDataBeingUsed(context)) {
                Log.d("MDTKR_UPDATE", "Mobile Data being used, cannot download!")
                return
            }
            if (!ConnectivityUtil.isInternetAvailable(context)) return
            AppUpdaterUtils(context)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("MoodTrkr", "volunteer-app-release")
                .withListener(object : UpdateListener {
                    override fun onSuccess(update: Update, isUpdateAvailable: Boolean?) {
                        callback.invoke(context, update)
                    }
                    override fun onFailed(error: AppUpdaterError) {
                        Log.e("MDTKR_UPDATE", "Something went wrong")
                        Log.e("MDTKR_UPDATE", error.toString())
                    }
                })
            .start()
        }

        fun checkForUpdates(context: Context) {
            checkForGitUpdates(context, this::downloadUpdatesCallback)
        }

        private fun downloadUpdatesCallback(context: Context, update: Update) {
            if (update.latestVersion == getPackageVersion()) {
                Log.i("MDTKR_UPDATE","Application on Latest Version: ${getPackageVersion()}")
                return
            }
            val updateDownloadStatus = SharedPreferencesStorage(context).retrieveBoolean(context.resources.getString(R.string.mdtkr_update_downloaded))

            Log.i("MDTKR_UPDATE","Update Available: ${update}")
            if (update.latestVersion == getPackageVersion() && updateDownloadStatus == true) {
                cleanUpdateFiles(context)
                return
            }
            if (update.latestVersion != null) Log.d("Latest Version", update.latestVersion)
            if (update.urlToDownload != null) Log.d("URL", update.urlToDownload.toString())
            if (update.latestVersion != null && update.urlToDownload != null) {
                val myFile = File(context.filesDir, "updates/${update.latestVersion}.zip")
                if (myFile.exists()) myFile.delete()
                if (!myFile.parentFile.exists()) myFile.parentFile.mkdirs()
                myFile.createNewFile()

                val data = Data.Builder()
                var url =
                    "MoodTrkr/volunteer-app-release/archive/refs/tags/${update.latestVersion}.zip"
                SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_latest_version), update.latestVersion)
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
                Log.d("MDTKR_ZIP", "I think somethn worked")
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
            var updateDownloadStatus = SharedPreferencesStorage(context).retrieveBoolean(context.resources.getString(R.string.mdtkr_update_downloaded))
            val updateDownloadPath = SharedPreferencesStorage(context).retrieveString(context.resources.getString(R.string.mdtkr_update_loc))
            if (updateDownloadStatus==false && File(context.filesDir,"/updates").exists()) cleanUpdateFiles(context)
            if (updateDownloadPath == null || updateDownloadStatus != true) return

            Log.d("MDTKR_UPDATE", "Update State: $updateDownloadStatus $updateDownloadPath")
            val latestUpdate = SharedPreferencesStorage(context).retrieveString(context.resources.getString(R.string.mdtkr_latest_version))
            if (updateDownloadStatus == true && latestUpdate == getPackageVersion()) {
                cleanUpdateFiles(context)
                return
            }

            if (updateDownloadStatus != true) return
            var updateFile = File(updateDownloadPath)
            if (!updateFile.exists() || updateFile.listFiles().isEmpty()) return
            Log.d("MDTKR_UPDATE", "List of Update Dir Files: ${updateFile.listFiles().size} - ${updateFile.listFiles()}")
            updateFile = updateFile.listFiles()[0]
            updateFile = File(updateFile.absolutePath+"/main.apk")

            Log.d("MDTKR_UPDATE", "Intent being created!")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType( FileProvider.getUriForFile(context, context.packageName+".provider", updateFile), "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            Log.d("MDTKR_UPDATE", "${PermissionsManager.isInstallAppsPermissionGranted(context)}")
            if (!PermissionsManager.isInstallAppsPermissionGranted(context)) return
            try {
                context.startActivity(intent)
            }
            catch (t: Throwable) {
                Log.e("MDTKR_UPDATE", t.message.toString())
            }
        }

        fun getPackageVersion(): String {
            return VERSION_NAME
        }

        /**
         *  Run this whenever updated. Deletes update files from app directory.
         * */
        fun cleanUpdateFiles(context: Context) {
            Log.i("MDTKR_UPDATE", "Cleaning updates!")
            val updateFolder = File(context.filesDir,"/updates")
            if (updateFolder.exists()) deleteFilesRecursively(updateFolder)
            SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_update_downloaded), false)
        }

        private fun deleteFilesRecursively(file: File) {
            Log.e("MDTKR", "ATTEMPTING TO DELETE: ${file.absolutePath}")
            if (file.isDirectory) {
                file.listFiles().forEach { child -> deleteFilesRecursively(child) }
            }
            file.deleteRecursively()
        }
    }
}
