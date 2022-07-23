package com.example.moodtrackr.router

import android.content.Context
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.management.UsersAPIClient
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.router.data.CompressedRequestBody
import com.example.moodtrackr.router.routes.UsageDataRoutes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


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
                    val auth = Auth0Manager.authSetup(context.applicationContext)
                    val job = runBlocking { Auth0Manager.retrieveAccessTokenAsync(context, auth.third) }
                    val newRequest: Request = chain.request().newBuilder()
                        .header("authorization", "Bearer ${job.getCompleted()}")
                        .build()
                    chain.proceed(newRequest)
                })
                .addInterceptor(Interceptor { chain ->
                    val newRequest: Request = chain.request().newBuilder()
//                        .header("authorization", "Bearer $token")
//                        .header("authorization", "Bearer $token")
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

        suspend fun <T, R> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend (R) -> T?, inp: R): CompletableDeferred<Pair<T?, Int>> {
            val deferred = CompletableDeferred<Pair<T?, Int>>()
            withContext(dispatcher) {
                try {
                    deferred.complete(Pair(
                        apiCall.invoke(inp),
                        0
                    ))
                } catch (t: Throwable) {
                    safeApiCallExceptionHandler(deferred, t)
                }
            }
            return deferred
        }

        suspend fun <T, R, S> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend (R, S) -> T?, inp1: R, inp2: S): CompletableDeferred<Pair<T?, Int>> {
            val deferred = CompletableDeferred<Pair<T?, Int>>()
            withContext(dispatcher) {
                try {
                    deferred.complete(Pair(
                        apiCall.invoke(inp1, inp2),
                        0
                    ))
                } catch (t: Throwable) {
                    safeApiCallExceptionHandler(deferred, t)
                }
            }
            return deferred
        }

        private fun <T> safeApiCallExceptionHandler(deferred: CompletableDeferred<Pair<T?, Int>>, t: Throwable) {
            when (t) {
                is HttpException -> {
                    Log.e("MT_REST", "HTTP_ERROR: ${t.message}")
                    deferred.complete(Pair(null, 1))
                }
                is IOException -> {
                    Log.e("MT_REST", "IO_ERROR: ${t.message}")
                    deferred.complete(Pair(null, 2))
                }
                else -> {
                    Log.e("MT_REST", "OTHER_ERROR: ${t.cause} ${t.message}")
                    deferred.complete(Pair(null, 3))
                }
            }
        }
        private fun <T, R> queueRequest(deferred: CompletableDeferred<Pair<T?, Int>>, inp1: T, inp2: R?) {
            if (inp1 is MTUsageData)
        }
    }
}