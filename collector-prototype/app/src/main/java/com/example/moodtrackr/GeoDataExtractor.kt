package com.example.moodtrackr

import android.Manifest
import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class GeoDataExtractor(activity: FragmentActivity) {
    private var activity: FragmentActivity = activity
    private var appContext: Context
    private var baseContext: Context
    private var locManager: FusedLocationProviderClient
    private val permsManager: PermissionsManager

    init {
        this.appContext = activity!!.applicationContext
        this.baseContext = activity!!.baseContext
        this.locManager = LocationServices.getFusedLocationProviderClient(baseContext)
        this.permsManager = PermissionsManager(activity)
    }

    private fun getLoc(): Location? {
        var loc: Location? = null
        try {
            locManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    loc = location
                }
        }
        catch (e: SecurityException) { permsManager.checkLocPermissions() }
        return loc
    }
}