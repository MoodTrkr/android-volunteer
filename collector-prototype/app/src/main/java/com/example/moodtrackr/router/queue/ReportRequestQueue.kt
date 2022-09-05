package com.example.moodtrackr.router.queue

import android.content.Context
import android.util.Log
import com.example.moodtrackr.db.router.RouterRequest
import com.example.moodtrackr.util.DatabaseManager
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.*


class ReportRequestQueue {
    companion object {
        suspend fun add(context: Context, type: String, params: String, payload: String) {
            DatabaseManager.getInstance(context).routerRequestsDAO.insert(
                RouterRequest(Date(), type, params, payload)
            )
        }

        fun peek(context: Context): RouterRequest? {
            var req: RouterRequest?
            runBlocking {
                req = DatabaseManager.getInstance(context).routerRequestsDAO.getOne()
            }
            return req
        }

        fun remove(context: Context, routerRequest: RouterRequest) {
            runBlocking {
                DatabaseManager.getInstance(context).routerRequestsDAO.delete(routerRequest)
            }
        }
    }
}