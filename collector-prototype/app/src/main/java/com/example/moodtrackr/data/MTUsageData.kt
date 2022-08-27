package com.example.moodtrackr.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moodtrackr.util.DatesUtil
import java.util.*

@Entity(tableName = "usage_records")
data class MTUsageData(
    @PrimaryKey @ColumnInfo(name = "date") var date: Long = DatesUtil.getTodayTruncated().time,
    @ColumnInfo(name = "complete") var complete: Boolean = false,
    @Embedded(prefix = "periodic_") var periodicCollBook: PeriodicCollectionBook = PeriodicCollectionBook(),
    @Embedded(prefix = "daily_") var dailyCollection: DailyCollection = DailyCollection(),
    @Embedded(prefix = "survey_") var surveyData: SurveyData = SurveyData()
)
{
    constructor() : this(Date().time, false, PeriodicCollectionBook(), DailyCollection(), SurveyData())
    constructor(date: Date) : this(DatesUtil.truncateDate(date).time, false, PeriodicCollectionBook(), DailyCollection(), SurveyData())
    constructor(date: Long) : this(DatesUtil.truncateDate(Date(date)).time, false, PeriodicCollectionBook(), DailyCollection(), SurveyData())

    constructor(complete: Boolean, date: Date, periodicCollBook: PeriodicCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.complete = complete
        this.date = DatesUtil.truncateDate(date).time
        this.periodicCollBook = periodicCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(date: Date, periodicCollBook: PeriodicCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = DatesUtil.truncateDate(date).time
        this.complete = false
        this.periodicCollBook = periodicCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    constructor(periodicCollBook: PeriodicCollectionBook, dailyCollection: DailyCollection, surveyData: SurveyData) : this() {
        this.date = DatesUtil.getTodayTruncated().time
        this.complete = false
        this.periodicCollBook = periodicCollBook
        this.dailyCollection = dailyCollection
        this.surveyData = surveyData
    }

    companion object {
        const val version = 1
    }
}