package com.example.moodtrackr.router.data

import android.content.Context
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R
import com.example.moodtrackr.data.DailyCollection
import com.example.moodtrackr.data.MTUsageData
import com.example.moodtrackr.data.PeriodicCollectionBook
import com.example.moodtrackr.data.SurveyData
import com.example.moodtrackr.util.DatesUtil
import com.example.moodtrackr.util.UpdateManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

data class MTUsageDataStamped(
    var usageDataVersion: Int,
    var date: Long = DatesUtil.getTodayTruncated().time,
    var complete: Boolean = false,
    var periodicCollBook: PeriodicCollectionBook = PeriodicCollectionBook(),
    var dailyCollection: DailyCollection = DailyCollection(),
    var surveyData: SurveyData = SurveyData(),
    var demographics: Map<String, Any>,
    var phoneDetails: Map<String, String>,
    var appVersion: String = UpdateManager.getPackageVersion()
)
{
    constructor(): this(MTUsageData.version, Date().time, false, PeriodicCollectionBook(), DailyCollection(), SurveyData(), mapOf<String, Any>(), mapOf<String, String>(), UpdateManager.getPackageVersion())
    companion object {
        private fun getDemographics(context: Context): Map<String, Any> {
            val demographicsStr = SharedPreferencesStorage(context).retrieveString(context.resources.getString(R.string.auth0_user_metadata))
            val mapType: Type = object : TypeToken<Map<String, Any>>() {}.type
            return Gson().fromJson(demographicsStr, mapType)
        }

        fun stampUsageData(context: Context, usageData: MTUsageData): MTUsageDataStamped {
            val usageDataStamped = MTUsageDataStamped()

            usageDataStamped.usageDataVersion = usageData.dailyCollection.version
            usageDataStamped.date = usageData.date
            usageDataStamped.complete = usageData.complete
            usageDataStamped.periodicCollBook = usageData.periodicCollBook
            usageDataStamped.dailyCollection = usageData.dailyCollection
            usageDataStamped.surveyData = usageData.surveyData
            usageDataStamped.demographics = getDemographics(context)

            var phoneDetails = mutableMapOf<String, String>()

            phoneDetails["SDK_INT"] = android.os.Build.VERSION.SDK_INT.toString()
            phoneDetails["DEVICE"] = android.os.Build.DEVICE.toString()
            phoneDetails["MODEL"] = android.os.Build.MODEL.toString()
            phoneDetails["PRODUCT"] = android.os.Build.PRODUCT.toString()

            usageDataStamped.phoneDetails = phoneDetails

            return usageDataStamped
        }
    }
}