package com.example.moodtrackr.router.routes

import com.example.moodtrackr.data.MTUsageData
import retrofit2.Call
import retrofit2.http.*
import java.io.ByteArrayInputStream

/**
 * Usage Data Routes
 */
interface UsageDataRoutes {
    /**
     * Find Usage Data by Date. Date data must provided in Long format.
     */
    @GET("$BASE_PATH/get")
    suspend fun getUsageData(@Query("date") query: Long): Result<MTUsageData>?

    /**
     * Obtain all Usage Data. Likely to not to be used.
     */
//    @GET("$BASE_PATH/get-all")
//    fun getAllUsageData(): Call<MTUsageData>?

    /**
     * Insert Usage Data Object. Date data must provided in Long format.
     */
    @POST("$BASE_PATH/insert")
    suspend fun insertUsageData(@Query("date") query: Long, @Body usage: MTUsageData): Result<Boolean>?
    @POST("$BASE_PATH/insert")
    suspend fun insertUsageData(@Query("date") query: Long, @Body usage: ByteArrayInputStream): Result<Boolean>?

    companion object {
        private const val BASE_PATH = "/api/v1/usage-data"
    }
}