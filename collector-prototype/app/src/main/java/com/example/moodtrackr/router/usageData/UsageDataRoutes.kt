package com.example.moodtrackr.router.usageData

import android.content.Context
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.data.MTUsageData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Usage Data Routes
 */
interface UsageDataRoutes {
    /**
     * Find Usage Data by Date. Date data must provided in Long format.
     */
    @GET("$BASE_PATH/get")
    fun getUsageData(@Query("date") query: Long): Call<MTUsageData>?

    /**
     * Obtain all Usage Data. Likely to not to be used.
     */
    @GET("$BASE_PATH/get-all")
    fun getAllUsageData(): Call<MTUsageData>?

    /**
     * Gets sleep time bounds by Date. Date data must provided in Long format.
     */
    @GET("$BASE_PATH/get-sleep-bounds")
    fun getSleepBounds(@Query("date") query: Long): Call<Pair<Long, Long>>?

    /**
     * Insert Usage Data Object. Date data must provided in Long format.
     */
    @POST("$BASE_PATH/insert")
    fun insertUsageData(@Body issue: MTUsageData): Boolean?

    companion object {
        private const val BASE_PATH = "/api/v1/usage-data"
    }
}