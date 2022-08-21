package com.example.moodtrackr.router

import android.content.Context
import android.util.Log
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.auth.Auth0Manager
import com.example.moodtrackr.collectors.workers.DownloadWorker
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.db.router.RouterRequest
import com.example.moodtrackr.router.data.CompressedRequestBody
import com.example.moodtrackr.router.data.MTUsageDataStamped
import com.example.moodtrackr.router.queue.ReportRequestQueue
import com.example.moodtrackr.router.routes.UpdateRoutes
import com.example.moodtrackr.router.routes.UsageDataRoutes
import com.example.moodtrackr.router.util.StreamDownloader
import com.example.moodtrackr.util.ConnectivityUtil
import com.example.moodtrackr.util.DatabaseManager
import com.example.moodtrackr.util.DatesUtil
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


/**
 * Rest Client
 */
interface RestClient : UsageDataRoutes, UpdateRoutes {
    companion object {
        private var token: String = ""
        @Volatile
        private var restClient: RestClient? = null
        @Volatile
        private var updateClient: RestClient? = null

        fun getUpdateInstance(context: Context): RestClient {
            return restClient ?: synchronized(this) {
                restClient ?: buildUpdateClient(context)
                    .also { restClient = it }
            }
        }

        fun getInstance(context: Context): RestClient {
            return restClient ?: synchronized(this) {
                restClient ?: buildRestClient(context)
                    .also { restClient = it }
            }
        }

        private fun buildUpdateClient(context: Context): RestClient {
            return Retrofit.Builder()
                .baseUrl("https://github.com/")
                .build()
                .create(RestClient::class.java)
        }

        private fun buildRestClient(context: Context): RestClient {
            val client: OkHttpClient = OkHttpClient.Builder()
//                .writeTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .addInterceptor(Interceptor { chain ->
                    val token: String? = SharedPreferencesStorage(context)
                        .retrieveString(context.resources.getString(R.string.token_identifier))
                    val newRequest: Request = chain.request().newBuilder()
//                        .header("authorization", "Bearer 123")
                        .header("authorization", "Bearer $token")
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

        suspend fun <T, R> safeApiCall(context: Context, dispatcher: CoroutineDispatcher, apiCall: suspend (R) -> T?, inp: R): CompletableDeferred<Pair<T?, Int>> {
            val deferred = CompletableDeferred<Pair<T?, Int>>()

            if (!ConnectivityUtil.isInternetAvailable(context))  {
                queueRequest(context, inp, null)
                deferred.complete(Pair<T?, Int>(null, 2))
                return deferred
            }
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

        suspend fun <T, R, S> safeApiCall(context: Context, dispatcher: CoroutineDispatcher, apiCall: suspend (R, S) -> T?, inp1: R, inp2: S): CompletableDeferred<Pair<T?, Int>> {
            val deferred = CompletableDeferred<Pair<T?, Int>>()

            if (!ConnectivityUtil.isInternetAvailable(context))  {
                queueRequest(context, inp1, inp2)
                deferred.complete(Pair<T?, Int>(null, 2))
                return deferred
            }

            withContext(dispatcher) {
                try {
                    deferred.complete(Pair(
                        apiCall.invoke(inp1, inp2),
                        0
                    ))
                } catch (t: Throwable) {
                    safeApiCallExceptionHandler(deferred, t)
                    launch(Dispatchers.Default) {
                        val def = deferred.getCompleted()
                        when (def.second) {
                            2 -> queueRequest(context, inp1, inp2)
                            4 -> {
                                Log.e("MT_REST", "ATTEMPTING REFRESH")
                                Auth0Manager(context).refreshCredentials()
                                queueRequest(context, inp1, inp2)
                            }
                        }
                    }
                }
            }
            return deferred
        }

        private fun <T> httpExceptionHandler(deferred: CompletableDeferred<Pair<T?, Int>>, exception: HttpException) {
            when (exception.code()) {
                401 -> deferred.complete(Pair(null, 4))
                else -> deferred.complete(Pair(null, 1))
            }
        }

        private fun <T> safeApiCallExceptionHandler(deferred: CompletableDeferred<Pair<T?, Int>>, t: Throwable) {
            when (t) {
                is HttpException -> {
                    httpExceptionHandler(deferred, t)
                    Log.e("MT_REST", "HTTP_ERROR: ${t.message}")
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

        suspend fun safeUpdateDownload(worker: DownloadWorker, context: Context, dispatcher: CoroutineDispatcher, url: String, path: String) {
            if (!ConnectivityUtil.isInternetAvailable(context)) return
            withContext(dispatcher) {
                try {
                    val body = getUpdateInstance(context).downloadUpdate(url).body()
                    StreamDownloader.saveFile(worker, context, body, path)
                } catch (t: Throwable) {
                    Log.e("MDTKR_REST","DOWNLOAD FAILED")
                    Log.e("MDTKR_REST","$t")
                }
            }
        }

        private suspend fun <T, R> queueRequest(context: Context, inp1: T, inp2: R?) {
            when (inp2) {
                null -> null
                is MTUsageData -> {
                    val gson = Gson()
                    DatabaseManager.getInstance(context).routerRequestsDAO.insert(
                        RouterRequest(
                            Date(),
                            RouterRequest.INSERT_USAGE,
                            inp1.toString(),
                            gson.toJson(inp2)
                        )
                    )
                }
                else -> Log.e("DEBUG", "Unsupported Rest request type")
            }
        }

        fun popRequest(context: Context, dispatcher: CoroutineDispatcher) {
            if (!ConnectivityUtil.isInternetAvailable(context))  {
                return
            }

            CoroutineScope(dispatcher).launch {
                val req = ReportRequestQueue.peek(context)
                req?.let {
                    when (it.type) {
                        RouterRequest.INSERT_USAGE -> {
                            val apiCall = RestClient.safeApiCall(
                                context,
                                dispatcher,
                                RestClient.getInstance(context)::insertUsageData,
                                DatesUtil.getYesterdayTruncated().time,
                                Gson().fromJson(it.payload, MTUsageDataStamped::class.java)
                            )
                        }
                        else -> null
                    }
                    ReportRequestQueue.remove(context, req)
                }
            }
        }
    }
}