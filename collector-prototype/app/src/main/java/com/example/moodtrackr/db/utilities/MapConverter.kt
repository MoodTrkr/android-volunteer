package com.example.moodtrackr.db.utilities

import androidx.room.TypeConverter
import com.example.moodtrackr.data.PeriodicCollection
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class MapConverter {
    @TypeConverter
    fun stringMapFromString(value: String): MutableMap<String, String> {
        val mapType: Type = object : TypeToken<MutableMap<String, String>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringMap(map: MutableMap<String, String>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun stringLongMapFromString(value: String): MutableMap<String, Long> {
        val mapType: Type = object : TypeToken<MutableMap<String, Long>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromStringLongMap(map: MutableMap<String, Long>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun dateLongMapFromString(value: String): MutableMap<Date, Long> {
        val mapType: Type = object : TypeToken<MutableMap<Date, Long>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromDateLongMap(map: MutableMap<Date, Long>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromLongMap(map: MutableMap<Long, Long>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun LongMapFromString(value: String): MutableMap<Long, Long> {
        val mapType: Type = object : TypeToken<MutableMap<Long, Long>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun IntMapFromString(value: String): MutableMap<Int, Int> {
        val mapType: Type = object : TypeToken<MutableMap<Int, Int>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromIntMap(map: MutableMap<Int, Int>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun IntHourlyCollFromString(value: String): MutableMap<Int, PeriodicCollection> {
        val mapType: Type = object : TypeToken<MutableMap<Int, PeriodicCollection>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromIntHourlyCollMap(map: MutableMap<Int, PeriodicCollection>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun UsageLogsMapFromString(value: String): MutableMap<Long, Pair<String, Int>> {
        val mapType: Type = object : TypeToken<MutableMap<Long, Pair<String, Int>>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromUsageLogsMapMap(map: MutableMap<Long, Pair<String, Int>>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}