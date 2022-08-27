package com.example.moodtrackr.router.routes

import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.router.data.MTUsageDataStamped
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.io.ByteArrayInputStream

/**
 * Usage Data Routes
 */
interface UpdateRoutes {
    @Streaming
    @GET
    suspend fun downloadUpdate(@Url url: String): Response<ResponseBody>
}