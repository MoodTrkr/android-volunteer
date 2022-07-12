package com.example.moodtrackr.router.data

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.GzipSink
import okio.buffer

class CompressedRequestBody(body: RequestBody) : RequestBody() {
    private val body = body
    override fun contentType(): MediaType? {
        return body.contentType()
    }

    override fun contentLength(): Long {
        return -1 // We don't know the compressed length in advance!
    }

    override fun writeTo(sink: BufferedSink) {
        val gzipSink = GzipSink(sink!!).buffer()
        body.writeTo(gzipSink)
        gzipSink.close()
    }
}