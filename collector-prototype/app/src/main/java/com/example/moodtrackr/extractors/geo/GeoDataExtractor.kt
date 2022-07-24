package com.example.moodtrackr.extractors.geo

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.util.PermissionsManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*


class GeoDataExtractor(activity: FragmentActivity?, permsManager: PermissionsManager) {
    private var activity: FragmentActivity = activity!!
    private var appContext: Context
    private var baseContext: Context
    private var locManager: FusedLocationProviderClient
    private val permsManager: PermissionsManager = permsManager

    private var longitude: Double? = null
    private var latitude: Double? = null

    init {
        this.appContext = activity!!.applicationContext
        this.baseContext = activity!!.baseContext
        this.locManager = LocationServices.getFusedLocationProviderClient(baseContext)
    }

    fun setLoc() {
        var loc: Location? = null
        try {
            locManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
        }
        catch (e: SecurityException) { permsManager.checkLocPermissions() }
    }

    fun getLoc(): Pair<Double?, Double?> {
        return Pair(latitude, longitude)
    }

    fun setCountry() {
        val geocoder = Geocoder(appContext, Locale.getDefault())
        try {
            locManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        Log.e("DEBUG", "COUNTRY: ${addresses[0].countryName}")
                        if (addresses.isNotEmpty()) country = addresses[0].countryName
                    }
                }
        }
        catch (e: SecurityException) { permsManager.checkLocPermissions() }
    }

    fun getCountry(): String? {
        return country
    }
    companion object {
        private var country: String? = null
    }
}