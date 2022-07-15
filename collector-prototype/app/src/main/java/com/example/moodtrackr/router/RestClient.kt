package com.example.moodtrackr.router

import android.content.Context
import android.util.Log
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.router.data.CompressedRequestBody
import com.example.moodtrackr.router.routes.UsageDataRoutes
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Rest Client
 */
interface RestClient : UsageDataRoutes {
    companion object {
        private var token: String = ""
        @Volatile
        private var restClient: RestClient? = null

        fun getInstance(context: Context): RestClient {
            return restClient ?: synchronized(this) {
                restClient ?: buildRestClient(context)
                    .also { restClient = it }
            }
        }

        private fun buildRestClient(context: Context): RestClient {
            val token: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_identifier))

            val client: OkHttpClient = OkHttpClient.Builder()
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .addInterceptor(Interceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
                        .addHeader("authorization", "Bearer $token")
                        .header("Content-Encoding", "gzip")
                        .method(chain.request().method,
                            CompressedRequestBody(chain.request().body!!))
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