package com.fgieracki.iotapplication.data.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Device' wifi manager for android SDK 29+
 */
@RequiresApi(Build.VERSION_CODES.Q)
class AppWiFiManager {

    private val tag = this::class.java.simpleName
    private var connectivityManager: ConnectivityManager? = null
    private var connectedToDevice = false
    private var connectedToUserNetwork = false

    private fun getConnectivityManager(context: Context): ConnectivityManager {
        if (connectivityManager == null) {
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        return connectivityManager!!
    }

     fun connectToDeviceWiFi(context: Context, ssid: String, password: String) {
//        Lo(tag, "connectToDeviceWiFi")
         println("connectToDeviceWiFi")

        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

//        Rlog.d(tag, "NetCapabilities ${networkRequest}")
         println("NetCapabilities ${networkRequest}")
        getConnectivityManager(context).requestNetwork(networkRequest, wifiStateChangeListener)
        connectedToDevice = true
    }

    fun disconnectFromDeviceWiFi(context: Context) {
        if (connectedToDevice) {
//            Rlog.d(tag, "disconnectFromDeviceWiFi")
            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
            connectedToDevice = false
        } else {
//            Rlog.d(tag, "Already disconnected FromDeviceWiFi, skipping")
            println("Already disconnected FromDeviceWiFi, skipping")
        }
    }

    fun connectToUserNetwork(
        context: Context,
        ssid: String,
        password: String
    ) {
//        Rlog.d(tag, "Connect to user network $ssid")
        println("Connect to user network $ssid")

//        makeDialog(context, ssid)
        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .build()

        val result = getConnectivityManager(context).requestNetwork(networkRequest, wifiStateChangeListener)
        connectedToUserNetwork = true
//        Rlog.d(tag, "Request Network result $result")
        println("Request Network result $result")
    }

    fun disconnectFromUserNetwork(context: Context) {
        if (connectedToUserNetwork) {
//            Rlog.d(tag, "disconnectFromDeviceWiFi")
            println("disconnectFromDeviceWiFi")
            getConnectivityManager(context).unregisterNetworkCallback(wifiStateChangeListener)
            connectedToUserNetwork = false
        } else {
//            Rlog.d(tag, "Already disconnected FromDeviceWiFi, skipping")
            println("Already disconnected FromDeviceWiFi, skipping")
        }
    }

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

    fun enableWiFi(context: Context) {
//        AlertDialog.Builder(context)
//            .setIcon(context.getDrawable(R.drawable.ideal_logo_green))
//            .setTitle(context.getString(R.string.wifi_enable))
//            .setMessage(context.getString(R.string.enable_wifi_and_return_to_app))
//            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
//                val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                context.startActivity(panelIntent)
//            }.show()
    }

    private val wifiStateChangeListener = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
//            Rlog.d(tag, "wifiStateChangeListener onAvailable network: $network")
            println( "wifiStateChangeListener onAvailable network: $network")
            connectivityManager!!.bindProcessToNetwork(network)
        }

        override fun onUnavailable() {
            super.onUnavailable()
//            Rlog.d(tag, "wifiStateChangeListener onUnavailable")
            println("wifiStateChangeListener onUnavailable")
            connectivityManager!!.bindProcessToNetwork(null)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
//            Rlog.d(tag, "wifiStateChangeListener onLost")
            println("wifiStateChangeListener onLost")
            connectivityManager!!.bindProcessToNetwork(null)
        }
    }
}
