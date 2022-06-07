package com.example.moodtrackr.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity


class PermissionsManager() {
    private lateinit var appContext: Context
    private lateinit var baseContext: Context
    private var requestMultiplePermissions: ActivityResultLauncher<Array<String>>? = null
    private val mandatoryPermissions: Array<String> = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.FOREGROUND_SERVICE
    )

    constructor(fragment: Fragment) : this() {
        Log.e("DEBUG", "Initialized on Fragment")
        this.appContext = fragment.requireActivity().applicationContext
        this.baseContext = fragment.requireActivity().baseContext
        this.requestMultiplePermissions = fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
            }
        }
    }

    constructor(activity: FragmentActivity) : this() {
        Log.e("DEBUG", "Initialized on Activity")
        this.appContext = activity.applicationContext
        this.baseContext = activity.baseContext
        this.requestMultiplePermissions = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
            }
        }
    }

    private fun requestPermissions(permissions: Array<String>) {
        requestMultiplePermissions!!.launch(permissions)
    }

    fun initPermissions() {
        requestPermissions(mandatoryPermissions)
    }

    fun checkAllPermissions() {
        Log.e("DEBUG", "Checking all Permissions!")
        var notProvidedPermissions: Array<String> = emptyArray()
        mandatoryPermissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED) { notProvidedPermissions += permission }
        }
        requestPermissions(notProvidedPermissions)
    }

    fun checkLocPermissions() {
        Log.e("DEBUG", "Check Loc Permissions!")
        if (ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ))
        }
    }
}