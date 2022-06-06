package com.example.moodtrackr.collectors

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.extractors.AppUsageExtractor
import com.example.moodtrackr.extractors.CallLogsStatsExtractor
import java.util.*

class CollectionUtil(context: FragmentActivity?) {
    private lateinit var usageExtractor: AppUsageExtractor
    private lateinit var callLogsExtractor: CallLogsStatsExtractor
    private lateinit var appContext: Context
    private lateinit var baseContext: Context

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context!!.baseContext
    }
}