package com.example.moodtrackr.extractors.calls

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import java.util.*
import java.util.concurrent.TimeUnit

class CallLogsStatsExtractor(baseContext: Context) {
    private lateinit var callLogsCursor: Cursor
    private lateinit var appContext: Context
    private lateinit var baseContext: Context

    init {
        this.baseContext = baseContext!!
        this.appContext = baseContext.applicationContext
        val cr: ContentResolver = baseContext!!.contentResolver
        this.callLogsCursor = cr.query(CallLog.Calls.CONTENT_URI, null, "1", null,
            CallLog.Calls.DATE)!!
    }

    constructor(activity: FragmentActivity?): this(activity!!.baseContext)


    private fun isCallWithinTimeRange(startTime: Long, endTime: Long): Boolean {
        val entryTime: Long = (callLogsCursor!!.getLong(callLogsCursor!!.
        getColumnIndexOrThrow(CallLog.Calls.DATE)))
        return entryTime in (startTime + 1) until endTime
    }

    fun queryLogs(startTime: Long, endTime: Long): MTCallStats {
        var callLogOutput: MutableMap<Date, Long> = mutableMapOf<Date, Long>()
        callLogsCursor!!.moveToLast()
        while (callLogsCursor != null &&
            isCallWithinTimeRange(startTime, endTime)) {
            val callDateStr: Long = callLogsCursor!!.getLong(callLogsCursor!!.getColumnIndexOrThrow(
                CallLog.Calls.DATE))
            val callDate: Date = Date(callDateStr)
            var callDuration: Long =
                callLogsCursor!!.getLong(callLogsCursor!!.getColumnIndexOrThrow(CallLog.Calls.DURATION))
            callDuration = TimeUnit.MILLISECONDS.toMinutes(callDuration)
            callLogOutput[callDate] = callDuration
            callLogsCursor!!.moveToPrevious()
        }
        return MTCallStats(callLogOutput)
    }
}