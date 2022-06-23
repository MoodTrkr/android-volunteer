package com.example.moodtrackr.data

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.moodtrackr.utilities.PermissionsManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
data class HourlyCollectionBook(
    var book: MutableMap<Int, HourlyCollection>
    )
{
    constructor() : this(mutableMapOf<Int, HourlyCollection>())
    operator fun get(idx: Int): HourlyCollection? { return this.book[idx] }
    operator fun set(idx: Int, value: HourlyCollection) { if (idx<144) this.book[idx] = value }
    fun size(): Int { return this.book.keys.size }
    fun isFull(): Boolean { return this.book.keys.size == 144 }
}

