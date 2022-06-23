package com.example.moodtrackr.data

import android.text.format.DateUtils
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moodtrackr.extractors.calls.data.MTCallStats
import com.example.moodtrackr.extractors.usage.data.MTAppUsageStats
import java.util.*

@Entity(tableName = "usage_records")
data class MTUsageData(
    @PrimaryKey @ColumnInfo(name = "date") var date: Date = Date(),
    @ColumnInfo(name = "complete") var complete: Boolean = false,
    @Embedded(prefix = "periodic_") var hourlyCollBook: HourlyCollectionBook = HourlyCollectionBook(),
    @Embedded(prefix = "daily_") var dailyCollection: DailyCollection = DailyCollection(),
    @Embedded(prefix = "survey_") var surveyData: SurveyData = SurveyData()
)
{
    constructor() : this(Date(), false, HourlyCollectionBook(), DailyCollection(), SurveyData())
    constructor(complete: Boolean, date: Date, hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.complete = complete
        this.date = truncateDate(date)
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
}