package com.example.moodtrackr.db.utilities

import androidx.room.TypeConverter
import com.example.moodtrackr.data.HourlyCollection
import com.google.gson.Gson
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
    fun IntHourlyCollFromString(value: String): MutableMap<Int, HourlyCollection> {
        val mapType: Type = object : TypeToken<MutableMap<Int, HourlyCollection>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromIntHourlyCollMap(map: MutableMap<Int, HourlyCollection>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}