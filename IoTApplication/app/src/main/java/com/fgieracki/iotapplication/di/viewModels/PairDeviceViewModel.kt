package com.fgieracki.iotapplication.di.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.api.model.TokenData
import com.fgieracki.iotapplication.data.local.ActivityCatcher
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.di.BLEManager
import com.fgieracki.iotapplication.di.EncryptionManager
import com.fgieracki.iotapplication.di.TokenGenerator
import com.fgieracki.iotapplication.domain.model.Resource
import com.juul.kable.AndroidAdvertisement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PairDeviceViewModel(private val repository: Repository = DefaultRepository()): ViewModel() {
    private val _scannedDevices = MutableStateFlow<List<AndroidAdvertisement>>(emptyList())
    val scannedDevices = _scannedDevices

    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel
    val isLoading = MutableStateFlow<Boolean>(false)

    private var chosenDevice: AndroidAdvertisement? = null
    val ssid: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val TOKEN_LENGTH = 12;

    private val bleManager = BLEManager()

    fun getScannedDevices() {
        if(bleManager.isBluetoothEnabled()){
            viewModelScope.launch {
                bleManager.getScannedDevices().let {
                    it.collect {
                        _scannedDevices.value = it
                    }
                }
            }
        }
    }

    init {
        requestAllPermissions()
        enableBluetoothIfDisabled()
        while (!bleManager.isBluetoothEnabled()) {
            Thread.sleep(1000)
        }
        getScannedDevices()
    }

    private fun requestAllPermissions() {
        if(ActivityCompat.checkSelfPermission(
                ActivityCatcher.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                ActivityCatcher.getActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                ), 1
            )
        }
    }

    private fun enableBluetoothIfDisabled() {
        if (!bleManager.isBluetoothEnabled()) {
            bleManager.enableBluetooth()
        }
    }

    fun onDeviceClick(device: AndroidAdvertisement) {
        chosenDevice = device
    }

    fun onPasswordChange(password: String) {
        this.password.value = password
    }

    fun onSsidChange(ssid: String) {
        this.ssid.value = ssid
    }

    fun onPairWithDevice() {
        val token = generateRandomToken()
        Log.d("PairDeviceViewModel", "Generated token: $token")
        val encryptionKey = generateEncryptionKey()
        val ivKey = generateIV()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                isLoading.value = true
                val response = repository.generateToken(TokenData(token, encryptionKey, ivKey))
                toastChannel.emit("Connecting with server...")
                if (response is Resource.Error) {
                    if (response.code == 401)
                        navChannel.emit("LOGOUT")
                    else {
                        toastChannel.emit("Something went wrong")
                        navChannel.emit("BACK")
                        return@launch
                    }
                } else if (response is Resource.Success && response.data != null) {
                    toastChannel.emit("Pairing with device...")
                    val userId = response.data!!.userId
                    val initDevicesCount = repository.getDevicesCount().data ?: 0
                    Log.i("PairDeviceViewModel", "initDevicesCount: $initDevicesCount")

                    Thread.sleep(10)
                    saveDataInSharedPrefs(getDeviceKey() + "AESKEY", encryptionKey)
                    Log.d(
                        "PairDeviceViewModel",
                        "Generated deviceKey:  " + getDeviceKey() + "AESKEY"
                    )
                    saveDataInSharedPrefs(getDeviceKey() + "AESIV", ivKey)


                    sendMessageToDevice(
                        ssid = ssid.value,
                        password = password.value,
                        token = token,
                        aesKey = encryptionKey,
                        aesIV = ivKey,
                        userId = userId,
                    )
                    for(i in 1..10) {
                        Thread.sleep(1000)
                        if (repository.getDevicesCount().data ?: 0 > initDevicesCount) {
                            toastChannel.emit("Added device successfully")
                            navChannel.emit("BACK")
                            return@launch
                        }
                    }
//                    val finalDevicesCount = repository.getDevicesCount().data ?: 0
//                    Log.i("PairDeviceViewModel", "finalDevicesCount: $finalDevicesCount")

//                    if (initDevicesCount == finalDevicesCount) {
                        isLoading.value = false
                        toastChannel.emit("Something went wrong")
                        navChannel.emit("BACK")
                        return@launch
//                    } else {
//                        toastChannel.emit("Added device successfully")
//                        navChannel.emit("BACK")
//                    }
                } else {
                    isLoading.value = false
                    toastChannel.emit("Something went wrong")
                    navChannel.emit("BACK")
                    return@launch
                }
            } catch (e: Exception) {
                isLoading.value = false
                toastChannel.emit("Something went wrong")
                navChannel.emit("BACK")
                return@launch
            }
        }
    }

    private fun generateRandomToken(): String {
        val tokenGenerator = TokenGenerator()
        return tokenGenerator.generateToken(TOKEN_LENGTH)
    }

    private fun generateEncryptionKey(): String {
        val encryptionManager = EncryptionManager()
        return encryptionManager.generateKey()
    }

    private fun generateIV(): String {
        val encryptionManager = EncryptionManager()
        return encryptionManager.generateIV()
    }

    private fun getDeviceKey(): String {
        return chosenDevice?.address?: ""
    }

    private fun saveDataInSharedPrefs(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private suspend fun sendMessageToDevice(ssid: String, password: String, token: String, aesKey: String, aesIV: String, userId: String) {
        if (chosenDevice != null)
            bleManager.sendMessageToDevice(
                advertisement = chosenDevice!!,
                token = token,
                ssid = ssid,
                password = password,
                aesIV = aesIV,
                aesKey = aesKey,
                userId = userId,

                )
        else Log.d("PairDeviceViewModel", "chosenDevice is null")
    }
}