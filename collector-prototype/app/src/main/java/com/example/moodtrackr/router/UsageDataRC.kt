package com.example.moodtrackr.router

import android.content.Context
import com.example.moodtrackr.R
import com.example.moodtrackr.data.MTUsageData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Usage Data Rest Client
 */
interface UsageDataRC {
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
        @Volatile private var restClient: UsageDataRC? = null

        fun getInstance(context: Context) : UsageDataRC {
            return UsageDataRC.restClient ?: synchronized(this) {
                UsageDataRC.restClient ?:
                buildRestClient(context)!!
                    .also { UsageDataRC.restClient = it }
            }
        }

        private fun buildRestClient(context: Context): UsageDataRC {
            val apiUrl = context.resources.getString((R.string.restApiUrl))
            return Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(apiUrl)
                        .build()
                        .create(UsageDataRC::class.java)
        }
    }
}