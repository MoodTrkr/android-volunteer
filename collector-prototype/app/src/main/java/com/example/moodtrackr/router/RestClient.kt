package com.example.moodtrackr.router

import android.content.Context
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.router.usageData.UsageDataRoutes
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Rest Client
 */
interface RestClient: UsageDataRoutes {
    companion object {
        @Volatile private var restClient: RestClient? = null

        fun getInstance(context: Context) : RestClient {
            return restClient ?: synchronized(this) {
                restClient ?:
                buildRestClient(context)!!
                    .also { restClient = it }
            }
        }

        private fun buildRestClient(context: Context): RestClient {
            val token = Auth0Manager(context.applicationContext).getCredentials()
            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(Interceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(newRequest)
            }).build()

            val apiUrl = context.resources.getString((R.string.restApiUrl))
            return Retrofit.Builder()
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(apiUrl)
                        .build()
                        .create(RestClient::class.java)
        }
    }
}