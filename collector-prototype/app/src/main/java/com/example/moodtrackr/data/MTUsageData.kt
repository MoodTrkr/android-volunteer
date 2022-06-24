package com.example.moodtrackr.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moodtrackr.util.DatesUtil
import java.util.*

@Entity(tableName = "usage_records")
data class MTUsageData(
    @PrimaryKey @ColumnInfo(name = "date") var date: Date = DatesUtil.getTodayTruncated(),
    @ColumnInfo(name = "complete") var complete: Boolean = false,
    @Embedded(prefix = "periodic_") var hourlyCollBook: HourlyCollectionBook = HourlyCollectionBook(),
    @Embedded(prefix = "daily_") var dailyCollection: DailyCollection = DailyCollection(),
    @Embedded(prefix = "survey_") var surveyData: SurveyData = SurveyData()
)
{
    constructor() : this(Date(), false, HourlyCollectionBook(), DailyCollection(), SurveyData())
    constructor(date: Date) : this(DatesUtil.truncateDate(date), false, HourlyCollectionBook(), DailyCollection(), SurveyData())

    constructor(complete: Boolean, date: Date, hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.complete = complete
        this.date = DatesUtil.truncateDate(date)
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(date: Date, hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = DatesUtil.truncateDate(date)
        this.complete = false
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(hourlyCollBook: HourlyCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = DatesUtil.getTodayTruncated()
        this.complete = false
        this.hourlyCollBook = hourlyCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }
}