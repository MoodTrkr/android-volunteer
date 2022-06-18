package com.example.moodtrackr.db.utilities

import androidx.room.TypeConverter
import java.util.*

class StringConverter {
    @TypeConverter
    fun toLong(str: String?): Long? {
        return str?.toLong()
    }

    @TypeConverter
    fun fromLong(long: Long?): String? {
        return if (long == null) null else long?.toString()
    }
}