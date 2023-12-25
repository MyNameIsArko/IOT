//package com.fgieracki.iotapplication.data.model
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.net.Network
//import android.net.NetworkCapabilities
//import android.net.NetworkRequest
//import android.net.wifi.WifiNetworkSpecifier
//import android.util.Log
//
///**
// * Device' wifi manager for android SDK 29+
// */
//class AppWiFiManager {
//
//    private val tag = this::class.java.simpleName
//    private var connectivityManager: ConnectivityManager? = null
//    private var connectedToDevice = false
//    private var connectedToUserNetwork = false
//
//    private fun getConnectivityManager(context: Context): ConnectivityManager {
//        if (connectivityManager == null) {
//            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        }
//        return connectivityManager!!
//    }
//
//     fun connectToDeviceWiFi(context: Context, ssid: String, password: String) {
//        Log.d(tag, "connectToDeviceWiFi")
//
//        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
//            .setWpa2Passphrase(password)
//            .build()
//
//        val networkRequest = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//            .setNetworkSpecifier(wifiNetworkSpecifier)
//            .build()
//
//        Log.d(tag, "NetCapabilities ${networkRequest}")
//        getConnectivityManager(context).requestNetwork(networkRequest, wifiStateChangeListener)
//        connectedToDevice = true
//    }
//
//    fun disconnectFromDeviceWiFi(context: Context) {
//        if (connectedToDevice) {
//            Log.d(tag, "disconnectFromDeviceWiFi")
//            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
//            connectedToDevice = false
//        } else {
//            Log.d(tag, "Already disconnected FromDeviceWiFi, skipping")
//        }
//    }
//
//    fun connectToUserNetwork(
//        context: Context,
//        ssid: String,
//        password: String
//    ) {
//        Log.d(tag, "Connect to user network $ssid")
//
//        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
//            .setWpa2Passphrase(password)
//            .build()
//
//        val networkRequest = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .setNetworkSpecifier(wifiNetworkSpecifier)
//            .build()
//
//        val result = getConnectivityManager(context).requestNetwork(networkRequest, wifiStateChangeListener)
//        connectedToUserNetwork = true
//        Log.d(tag, "Request Network result $result")
//    }
//
//    fun disconnectFromUserNetwork(context: Context) {
//        if (connectedToUserNetwork) {
//            Log.d(tag, "disconnectFromDeviceWiFi")
//            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
//            connectedToUserNetwork = false
//        } else {
//            Log.d(tag, "Already disconnected FromDeviceWiFi, skipping")
//        }
//    }
//
//    private val wifiStateChangeListener = object : ConnectivityManager.NetworkCallback() {
//
//        override fun onAvailable(network: Network) {
//            super.onAvailable(network)
//            Log.d(tag, "wifiStateChangeListener onAvailable network: $network")
//            connectivityManager!!.bindProcessToNetwork(network)
//        }
//
//        override fun onUnavailable() {
//            super.onUnavailable()
//            Log.d(tag, "wifiStateChangeListener onUnavailable")
//        }
//
//        override fun onLost(network: Network) {
//            super.onLost(network)
//            Log.d(tag, "wifiStateChangeListener onLost")
//            connectivityManager!!.bindProcessToNetwork(null)
//        }
//    }
//}
