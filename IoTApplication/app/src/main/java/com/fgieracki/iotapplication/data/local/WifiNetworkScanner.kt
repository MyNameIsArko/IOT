package com.fgieracki.iotapplication.data.local

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat

class WifiNetworkScanner(private val context: Context = ContextCatcher.getContext()) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    fun scanWifiNetworks(callback: (List<ScanResult>) -> Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startScan()
            val scanResults = wifiManager.scanResults
            callback(scanResults)
        } else {
            callback(emptyList())
        }
    }
}