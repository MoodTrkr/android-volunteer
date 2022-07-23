package com.example.moodtrackr.router.client

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


sealed class MTRestResultWrapper<out T> {
    data class Success<out T>(val value: T): MTRestResultWrapper<T>()
    data class Exception(val code: Int? = null, val error: Exception? = null): MTRestResultWrapper<Exception>()
}