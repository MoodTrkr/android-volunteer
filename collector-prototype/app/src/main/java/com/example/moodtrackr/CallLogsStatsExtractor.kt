package com.example.moodtrackr

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class CallLogsStatsExtractor(context: FragmentActivity?) {
    private var callLogsCursor: Cursor? = null
    private var appContext: Context? = null
    private var baseContext: Context? = null

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context!!.baseContext
        val cr: ContentResolver = baseContext!!.contentResolver
        this.callLogsCursor = cr.query(CallLog.Calls.CONTENT_URI, null, "1", null, CallLog.Calls.DATE)

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

    fun instantReturn(): ArrayList<String> {
        val calendar = Calendar.getInstance()
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24*3
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

//        Log.e("DEBUG", isUserUnlocked(context!!).toString())
//        var unlocked = (context!!.getSystemService(Context.USER_SERVICE) as UserManager).isUserUnlocked()
//        Log.e("DEBUG", unlocked.toString())
//        return usm!!.queryEvents(startTime, endTime)
        var callLogOutput: ArrayList<String> = ArrayList()
        callLogOutput.add("All Calls Within 24 Hours:")
        callLogOutput.add("======================")

        callLogsCursor!!.moveToLast()
        while (callLogsCursor != null &&
            isCallWithinTimeRange(endTime)) {

            var phoneNumber: String =
                callLogsCursor!!.getString(callLogsCursor!!.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
            var callDate: String = callLogsCursor!!.getString(callLogsCursor!!.getColumnIndexOrThrow(CallLog.Calls.DATE))
            callDate = Date(callDate.toLong()).toString()
            var callDuration: String =
                callLogsCursor!!.getString(callLogsCursor!!.getColumnIndexOrThrow(CallLog.Calls.DURATION))
            callDuration = String.format("%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(callDuration.toLong()) % 24,
                TimeUnit.SECONDS.toMinutes(callDuration.toLong()) % 60,
                TimeUnit.SECONDS.toSeconds(callDuration.toLong()) % 60
            )
            callLogOutput.add("Phone Call")
            callLogOutput.add("Phone Number: $phoneNumber")
            callLogOutput.add("Call Date: $callDate")
            callLogOutput.add("Call Duration: $callDuration")
            callLogOutput.add("======================")
            callLogsCursor!!.moveToPrevious()
        }
        callLogOutput.add("Hit Last Phone Call!")
        return callLogOutput
    }
}