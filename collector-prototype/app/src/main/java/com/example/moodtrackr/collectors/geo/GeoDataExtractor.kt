package com.example.moodtrackr.collectors.geo

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.utilities.PermissionsManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GeoDataExtractor(activity: FragmentActivity?, permsManager: PermissionsManager) {
    private var activity: FragmentActivity = activity!!
    private var appContext: Context
    private var baseContext: Context
    private var locManager: FusedLocationProviderClient
    private val permsManager: PermissionsManager = permsManager

    init {
        this.appContext = activity!!.applicationContext
        this.baseContext = activity!!.baseContext
        this.locManager = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    fun getLoc(): Location? {
        var loc: Location? = null
        try {
            locManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.e("DEBUG", location.toString())
                }
        }
        catch (e: SecurityException) { permsManager.checkLocPermissions() }
        return loc
    }
}