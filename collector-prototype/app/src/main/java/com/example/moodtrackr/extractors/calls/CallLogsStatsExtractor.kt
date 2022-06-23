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

    private fun isCallWithinTimeRange(startTime: Long, endTime: Long): Boolean {
        val entryTime: Long = (callLogsCursor!!.getLong(callLogsCursor!!.
        getColumnIndexOrThrow(CallLog.Calls.DATE)))
        return entryTime > startTime && entryTime < endTime
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