package com.example.moodtrackr

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.UserManager
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.util.*


class AppUsageExtractor(context: FragmentActivity?) {
    private var usm: UsageStatsManager? = null
    private var context: Context? = null

    init {
        this.context = context!!.applicationContext
        setUSM(context!!.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager)
    }

    private fun getUSM(): UsageStatsManager {
        return usm!!
    }

    private fun setUSM(inUsm: UsageStatsManager) {
        usm = inUsm
    }

    fun instantReturn(): UsageEvents? {
        val calendar = Calendar.getInstance()
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000*3600*24*3
        Log.e("DEBUG", startTime.toString())
        Log.e("DEBUG", endTime.toString())

//        Log.e("DEBUG", isUserUnlocked(context!!).toString())
//        var unlocked = (context!!.getSystemService(Context.USER_SERVICE) as UserManager).isUserUnlocked()
//        Log.e("DEBUG", unlocked.toString())
        return usm!!.queryEvents(startTime, endTime)
    }
}