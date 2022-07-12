package com.example.moodtrackr.router.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream


class CompressionUtil {
    companion object {
        fun compress(obj: Any): ByteArrayInputStream {
            val gson = Gson().toJson(obj).toByteArray()
            var bos: ByteArrayOutputStream = ByteArrayOutputStream()
            val gos = GZIPOutputStream(bos)
            gos.write(gson)
            gos.flush()
            gos.close()
            return ByteArrayInputStream(bos.toByteArray())
        }
    }
}