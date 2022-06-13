package com.example.moodtrackr.extractors.calls

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.util.*
import java.util.concurrent.TimeUnit

class CallLogsStatsExtractor(context: FragmentActivity?) {
    private var callLogsCursor: Cursor? = null
    private var appContext: Context? = null
    private var baseContext: Context? = null

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context!!.baseContext
        val cr: ContentResolver = baseContext!!.contentResolver
        this.callLogsCursor = cr.query(CallLog.Calls.CONTENT_URI, null, "1", null,
            CallLog.Calls.DATE)

        setCallLogsCursor(callLogsCursor!!)
    }

    private fun getCallLogsCursor(): Cursor {
        return callLogsCursor!!
    }

    private fun setCallLogsCursor(inCallLogsCursor: Cursor) {
        callLogsCursor = inCallLogsCursor
    }

    private fun isCallWithinTimeRange(currTime: Long): Boolean {
        return (currTime - callLogsCursor!!.getLong(callLogsCursor!!.
            getColumnIndexOrThrow(CallLog.Calls.DATE))) < 1000*3600*24
    }

    fun instantReturn(): MutableMap<Date, Long> {
        val calendar = Calendar.getInstance()
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24*3
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

        var callLogOutput: MutableMap<Date, Long> = mutableMapOf<Date, Long>()
        callLogsCursor!!.moveToLast()
        while (callLogsCursor != null &&
            isCallWithinTimeRange(endTime)) {
            val callDateStr: Long = callLogsCursor!!.getLong(callLogsCursor!!.getColumnIndexOrThrow(
                CallLog.Calls.DATE))
            val callDate: Date = Date(callDateStr)
            var callDuration: Long =
                callLogsCursor!!.getLong(callLogsCursor!!.getColumnIndexOrThrow(CallLog.Calls.DURATION))
            callDuration = TimeUnit.MILLISECONDS.toMinutes(callDuration)
            callLogOutput[callDate] = callDuration
            callLogsCursor!!.moveToPrevious()
        }
        return callLogOutput
    }
}