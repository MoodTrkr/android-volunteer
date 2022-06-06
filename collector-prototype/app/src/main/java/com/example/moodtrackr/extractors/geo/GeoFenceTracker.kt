package com.example.moodtrackr.extractors.geo

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class GeoFenceTracker(activity: FragmentActivity?) {
    private var activity: FragmentActivity = activity!!
    private var appContext: Context
    private var baseContext: Context
    private var locManager: FusedLocationProviderClient

    init {
        this.appContext = activity!!.applicationContext
        this.baseContext = activity!!.baseContext
        this.locManager = LocationServices.getFusedLocationProviderClient(baseContext)
    }
}