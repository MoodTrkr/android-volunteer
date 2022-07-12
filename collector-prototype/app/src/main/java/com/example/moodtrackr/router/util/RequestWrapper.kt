package com.example.moodtrackr.router.util

import android.util.Log
import com.example.moodtrackr.data.MTUsageData
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit


class RequestWrapper {
    companion object {
        fun wrapSync(date: Long, f: (date: Long) -> Call<MTUsageData>?): Response<MTUsageData>? {
            var get: Response<MTUsageData>?
            runBlocking {
                get = f(date)?.execute()
                Log.e("DEBUG", "Server Results: $get")
            }
            return get
        }
        fun wrapSync(usage: MTUsageData, f: (query: Long, usage: MTUsageData) -> Boolean?): Boolean? {
            var push: Boolean?
            runBlocking {
                push = f(usage.date, usage)
                Log.e("DEBUG", "Server Insert Results: $push")
            }
            return push
        }
    }
}