package com.example.moodtrackr.collectors

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.extractors.usage.AppUsageExtractor
import com.example.moodtrackr.extractors.calls.CallLogsStatsExtractor

class CollectionUtil(context: FragmentActivity?) {
    private lateinit var usageExtractor: AppUsageExtractor
    private lateinit var callLogsExtractor: CallLogsStatsExtractor
    private lateinit var appContext: Context
    private lateinit var baseContext: Context

    init {
        this.appContext = context!!.applicationContext
        this.baseContext = context!!.baseContext
    }

    fun dailyCollection() {

    }
}