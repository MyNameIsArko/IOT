//package com.fgieracki.iotapplication.ui.application.viewModels
//
//package de.com.ideal.airpro.utils.wifi
//
//import android.app.AlertDialog
//import android.content.Context
//import android.content.Intent
//import android.net.ConnectivityManager
//import android.net.Network
//import android.net.NetworkCapabilities
//import android.net.NetworkRequest
//import android.net.wifi.WifiNetworkSpecifier
//import android.os.Build
//import android.provider.Settings
//import androidx.annotation.RequiresApi
//import de.com.ideal.airpro.R
//import de.com.ideal.airpro.utils.extensions.getText
//import de.com.ideal.airpro.utils.Log.Log
//
///**
// * Device' wifi manager for android SDK 29+
// */
//@RequiresApi(Build.VERSION_CODES.Q)
//class AppWiFiManagerQ : AppWiFiManager() {
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
//    override fun connectToDeviceWiFi(context: Context, ssid: String) {
//        Log.d(tag, "connectToDeviceWiFi")
//
//        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
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
//    override fun disconnectFromDeviceWiFi(context: Context) {
//        if (connectedToDevice) {
//            Log.d(tag, "disconnectFromDeviceWiFi")
//            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
//            connectedToDevice = false
//        } else {
//            Log.d(tag, "Already disconnected FromDeviceWiFi, skipping")
//        }
//    }
//
//    override fun connectToUserNetwork(
//        context: Context,
//        ssid: String,
//        password: String
//    ) {
//        Log.d(tag, "Connect to user network $ssid")
//
////        makeDialog(context, ssid)
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
//    override fun disconnectFromUserNetwork(context: Context) {
//        if (connectedToUserNetwork) {
//            Log.d(tag, "disconnectFromDeviceWiFi")
//            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
//            connectedToUserNetwork = false
//        } else {
//            Log.d(tag, "Already disconnected FromDeviceWiFi, skipping")
//        }
//    }
//
//    private fun makeDialog(context: Context, ssid: String) {
//        AlertDialog.Builder(context)
//            .setIcon(context.getDrawable(R.drawable.ideal_logo_green))
//            .setTitle(context.getString(R.string.connect_to_wifi))
//            .setMessage(context.getText(R.string.connect_to_user_wifi, ssid))
//            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
//                val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                context.startActivity(panelIntent)
//            }.show()
//    }
//
//    override fun enableWiFi(context: Context) {
//        AlertDialog.Builder(context)
//            .setIcon(context.getDrawable(R.drawable.ideal_logo_green))
//            .setTitle(context.getString(R.string.wifi_enable))
//            .setMessage(context.getString(R.string.enable_wifi_and_return_to_app))
//            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
//                val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                context.startActivity(panelIntent)
//            }.show()
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
