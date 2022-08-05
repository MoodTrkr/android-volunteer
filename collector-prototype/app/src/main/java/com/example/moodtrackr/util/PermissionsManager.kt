package com.example.moodtrackr.util

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.moodtrackr.userInterface.permissions.PermissionsFragment


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
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
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

    fun isUsageAccessGranted(): Boolean {
        return try {
            val packageManager: PackageManager = appContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(appContext.packageName, 0)
            val appOpsManager = appContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
            var mode = 0
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager!!.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun grantUsageAccessPermission(fragment: Fragment) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.data = Uri.fromParts("package", appContext.packageName, null)
        fragment.startActivity(intent)

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

    fun allPermissionsGranted():Boolean {
        mandatoryPermissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    appContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED) { return false}
        }
        return true;
    }
}