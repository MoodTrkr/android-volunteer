package com.example.moodtrackr.router.util

import android.content.Context
import android.util.Log
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.collectors.workers.UpdateDownloadWorker
import com.example.moodtrackr.util.UpdateManager
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class StreamDownloader {
    companion object {
        fun saveFile(worker: UpdateDownloadWorker, context: Context, body: ResponseBody?, path: String):String{
            if (body==null)
                return ""
            var input: InputStream? = null
            try {
                input = body.byteStream()
                var bytesRead = 0L
                val totalBytes = body.contentLength()
                val fos = FileOutputStream(path)
                Log.e("MDTKR_REST", "CONTENT_LENGTH: $totalBytes")
                fos.use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    while (true) {
                        val bytes = input.read(buffer)
                        if (bytes == -1) {
                            worker.cancelNotification()
                            break
                        }
                        bytesRead += bytes
                        worker.updateProgress((bytesRead/1000000).toInt())
                        output.write(buffer, 0, bytes)
                    }
                    output.flush()
                }
                val zipFile = File(path)
                val unzippedPath = zipFile.parent+"/"+zipFile.name.substring(0, zipFile.name.length-4)
                UpdateManager.unzipFile(path, unzippedPath)
                SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_update_downloaded), true)
                SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_update_loc), unzippedPath)
                worker.completeNotification()
                return path
            } catch (e:Exception) {
                Log.e("saveFile", e.toString())
            }
            finally {
                input?.close()
            }
            return ""
        }
    }
}