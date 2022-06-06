package com.example.moodtrackr.extractors.network

import android.content.Context
import android.net.*
import android.util.Log


class NetworkCallbackCustom(context: Context): ConnectivityManager.NetworkCallback() {
    private var cm: ConnectivityManager? = null
    private var context: Context? = null
    var networkRequest: NetworkRequest? = null

    init {
        this.context = context
        this.networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
        this.cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    // network is available for use
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Log.e("DEBUG", "Network Available!")
    }

    // Network capabilities have changed for the network
    override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        Log.e("DEBUG", "Network Capabilities Changed!")
    }

    // lost network connection
    override fun onLost(network: Network) {
        super.onLost(network)
        Log.e("DEBUG", "Network Connection Lost !")
    }
}