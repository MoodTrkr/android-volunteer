package com.example.moodtrackr.data

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.room.ColumnInfo
import com.example.moodtrackr.utilities.PermissionsManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MTUsageData() {
    @ColumnInfo(name = "complete")
    lateinit var date: Date
    @ColumnInfo(name = "complete")
    var complete: Boolean = false
    lateinit var hourlyCollection: HourlyCollection
    lateinit var dailyCollection: DailyCollection
    lateinit var surveyData: SurveyData

    constructor(date: Date, hourlyCollection: HourlyCollection, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = date
        this.complete = false
        this.hourlyCollection = hourlyCollection
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(complete:Boolean, date: Date, hourlyCollection: HourlyCollection, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.complete = complete
        this.date = date
        this.complete = false
        this.hourlyCollection = hourlyCollection
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }
}