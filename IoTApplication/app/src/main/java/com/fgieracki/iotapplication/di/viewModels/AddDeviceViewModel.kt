package com.fgieracki.iotapplication.di.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.local.ActivityCatcher
import com.fgieracki.iotapplication.data.local.ContextCatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

@SuppressLint("MissingPermission")
class AddDeviceViewModel(private val repository: Repository = DefaultRepository()) : ViewModel() {
    val ssid: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

//    val appWiFiManager: AppWiFiManager = AppWiFiManager()

    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)

    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel

    val wifiManager = ContextCatcher.getContext()
        .applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiNetworkListState: MutableStateFlow<List<ScanResult>> = MutableStateFlow(listOf())

    val wifiScanReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action.equals("android.net.wifi.SCAN_RESULTS")) {
                val success = intent.getBooleanExtra("resultsUpdated", false)
                if (success) {
                    Log.d("AddDeviceViewModel", "scanSuccess")
                    updateWifiNetworkList()

                } else {
                    Log.d("AddDeviceViewModel", "scanFailure")
                }
            }
        }
    }

    init {
//        checkIfLocationIsEnabled()
        requestAllPermissions()
        checkIfLocationIsEnabled()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        ContextCatcher.getContext().registerReceiver(wifiScanReceiver, intentFilter)
        updateWifiNetworkList()
    }

    private fun requestAllPermissions() {
        ActivityCompat.requestPermissions(
            ActivityCatcher.getActivity(),
            arrayOf(
                Manifest.permission.INTERNET,

                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,

                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ), 1
        )
    }

    private fun checkIfLocationIsEnabled() {
        if (ActivityCompat.checkSelfPermission(
                ContextCatcher.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("AddDeviceViewModel", "ACCESS_FINE_LOCATION permission not granted... Requesting")
            ActivityCompat.requestPermissions(
                ActivityCatcher.getActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        if (!wifiManager.isScanAlwaysAvailable) {
            Log.d("AddDeviceViewModel", "Location not enabled... Requesting")
            val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCatcher.getContext().startActivity(intent)
        }
    }

    fun updateWifiNetworkList() {
        if (ActivityCompat.checkSelfPermission(
                ContextCatcher.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("AddDeviceViewModel", "ACCESS_FINE_LOCATION permission not granted... Requesting")
            ActivityCompat.requestPermissions(
                ActivityCatcher.getActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        Log.d("AddDeviceViewModel", "permission not granted FINE LOCATION")

        sendToastIfWifiDisabled()

        wifiNetworkListState.value = wifiManager.scanResults
    }

    private fun sendToastIfWifiDisabled() {
        if (!checkIfWifiEnabled()) {
            toastChannel.tryEmit("Please enable WiFi and connect to device")
        }
    }

    private fun checkIfWifiEnabled(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                ContextCatcher.getContext(),
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("AddDeviceViewModel", "ACCESS_WIFI_STATE permission not granted... Requesting")
            ActivityCompat.requestPermissions(
                ActivityCatcher.getActivity(),
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                1
            )
        }
        return wifiManager.isWifiEnabled
    }

    fun requestWiFiAndLocationPermissionsForTutorial() {
        checkIfLocationIsEnabled()
        if (!checkIfWifiEnabled()) sendToastIfWifiDisabled()
    }


    fun selectNetwork(scanResult: ScanResult) {
        this.ssid.value = scanResult.wifiSsid.toString()
    }

    fun onPasswordChange(password: String) {
        this.password.value = password
    }

    fun connectDevice() {
        toastChannel.tryEmit("Connecting to device...")
        Log.d("AddDeviceViewModel", "ssid: ${ssid.value}, password: ${password.value}")
//        appWiFiManager.disconnectFromUserNetwork(ContextCatcher.getContext())
//        appWiFiManager.connectToUserNetwork(ContextCatcher.getContext(), ssid.value, password.value)
//
//            appWiFiManager.disconnectFromUserNetwork(ContextCatcher.getContext())

//        viewModelScope.launch() {
//            val resource = repository.addDevice(ssid.value, password.value)
//            if (resource.data != null) {
//                navChannel.emit("deviceList")
//            } else {
//                toastChannel.tryEmit("Failed to connect to device. Try again!")
//            }
//        }

    }

}