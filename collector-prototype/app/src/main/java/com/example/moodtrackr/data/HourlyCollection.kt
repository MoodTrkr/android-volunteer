package com.example.moodtrackr.data

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.moodtrackr.utilities.PermissionsManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

data class HourlyCollection(
    val time: Date,
    val steps: Long,
    val unlocks: Long
    )
{
    constructor() : this(Date(), 0, 0)
}
