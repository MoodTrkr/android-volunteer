package com.example.moodtrackr.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.moodtrackr.R


class ConnectivityUtil {
    companion object {
        fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }

            return result
        }

        fun isMobileDataBeingUsed(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> false
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
                    else -> false
                }
            } else {
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> false
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> false
                            else -> false
                        }

                    }
                }
            }
            return result
        }

        fun toggleMobileDataPreferences(context: Context): Boolean {
            var pref = getMobileDataPreferences(context)
            setMobileDataPreferences(context, !pref)
            return !pref
        }

        fun setMobileDataPreferences(context: Context, pref: Boolean) {
            SharedPreferencesStorage(context).store(context.resources.getString(R.string.mdtkr_mobile_data_prefs), pref)
        }

        fun getMobileDataPreferences(context: Context): Boolean {
            var pref = SharedPreferencesStorage(context).retrieveBoolean(context.resources.getString(R.string.mdtkr_mobile_data_prefs))
            if (pref == null) pref = initMobileDataPreferences(context)
            return pref
        }

        private fun initMobileDataPreferences(context: Context): Boolean {
            setMobileDataPreferences(context, false)
            return false
        }
    }
}