package com.example.moodtrackr.data

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.*

class MTUsageData() {
    @PrimaryKey @ColumnInfo(name = "date") lateinit var date: Date
    @ColumnInfo(name = "complete") var complete: Boolean = false
    @ColumnInfo(name = "hourly") lateinit var hourlyCollBook: HourlyCollectionBook
    lateinit var dailyCollection: DailyCollection
    lateinit var surveyData: SurveyData

    constructor(complete: Boolean, date: Date, hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.complete = complete
        this.date = truncateDate(date)
        this.complete = false
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(date: Date, hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = truncateDate(date)
        this.complete = false
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = truncateDate(Date())
        this.complete = false
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    private fun truncateDate(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.time
    }

    fun dbInsert() {

    }
}