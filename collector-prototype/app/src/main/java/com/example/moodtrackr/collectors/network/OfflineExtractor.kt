package com.example.moodtrackr.collectors.network

import android.content.Context
import android.net.*
import androidx.fragment.app.FragmentActivity


class OfflineExtractor(activity: FragmentActivity?) {
    private var cm: ConnectivityManager? = null
    private var context: Context? = null
    private var networkRequest: NetworkRequest? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        this.context = activity!!.applicationContext
        this.cm = activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        this.networkCallback = NetworkCallbackCustom(this.context!!)
        this.networkRequest = (networkCallback!! as NetworkCallbackCustom).networkRequest

        cm!!.registerNetworkCallback(networkRequest!!, networkCallback!!)
    }
}