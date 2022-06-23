package com.example.moodtrackr.utilities

import com.example.moodtrackr.R
import java.util.*

class DatesUtil() {
    companion object {
        fun getToday(): Date {
            var cal: Calendar = Calendar.getInstance()
            return cal.time
        }

        fun getTodayTruncated(): Date {
            return truncateDate(getToday())
        }

        fun getYesterday(day: Date): Date {
            var cal: Calendar = Calendar.getInstance()
            cal.time = day
            cal.add(Calendar.DATE, -1)
            return truncateDate(cal.time)
        }

        fun getYesterday(): Date {
            var cal: Calendar = Calendar.getInstance()
            return getYesterday(cal.time)
        }

        fun getTomorrow(day: Date): Date {
            var cal: Calendar = Calendar.getInstance()
            cal.time = day
            cal.add(Calendar.DATE, 1)
            return truncateDate(cal.time)
        }

        fun getTomorrow(): Date {
            var cal: Calendar = Calendar.getInstance()
            return getTomorrow(cal.time)
        }

        fun getDayBounds(date: Date): Pair<Long, Long> {
            var dateTruncated: Date = truncateDate(date)
            var tomorrow: Date = truncateDate(getTomorrow(date))

            val dateStart: Long = dateTruncated.time
            val dateEnd: Long = tomorrow.time-1
            return Pair(dateStart, dateEnd)
        }

        fun getYesterdayBounds(): Pair<Long, Long> {
            return getDayBounds(getYesterday())
        }

        fun truncateDate(date: Date): Date {
            val cal = Calendar.getInstance()
            cal.time = date
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.time
        }

        fun todayQueryWrapper(f: (startTime: Long, endTime: Long) -> Any): Any {
            val bounds: Pair<Long, Long> = getDayBounds(getToday())
            return f(bounds.first, bounds.second)
        }

        fun yesterdayQueryWrapper(f: (startTime: Long, endTime: Long) -> Any): Any {
            val bounds: Pair<Long, Long> = getDayBounds(getYesterday())
            return f(bounds.first, bounds.second)
        }

        fun boundsInsertionWrapper(date: Date, f: (startTime: Long, endTime: Long) -> Any): Any {
            val bounds: Pair<Long, Long> = getDayBounds(date)
            return f(bounds.first, bounds.second)
        }
    }
}