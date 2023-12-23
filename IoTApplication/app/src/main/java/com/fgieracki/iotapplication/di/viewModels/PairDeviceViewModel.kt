package com.fgieracki.iotapplication.di.viewModels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.juul.kable.AndroidAdvertisement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PairDeviceViewModel(private val repository: Repository = DefaultRepository()): ViewModel() {
    private val _scannedDevices = MutableStateFlow<List<AndroidAdvertisement>>(emptyList())
    val scannedDevices = _scannedDevices

    private var chosenDevice: AndroidAdvertisement? = null
    val ssid: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    val navChannel = MutableStateFlow<String>("")
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
        val encryptionKey = generateEncryptionKey()

        viewModelScope.launch(Dispatchers.IO) {
            sendTokenToServer(token)
            Thread.sleep(10)
            saveDataInSharedPrefs(getDeviceKey(), encryptionKey)
            sendMessageToDevice(ssid = ssid.value,
                                password = password.value,
                                token = token,
                                hash = encryptionKey
            )
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

    private fun getDeviceKey(): String {
        return chosenDevice?.address?: ""
    }

    private fun saveDataInSharedPrefs(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private suspend fun sendTokenToServer(token: String) {
        repository.generateToken(TokenData(token))
    }

    private suspend fun sendMessageToDevice(ssid: String, password: String, token: String, hash: String) {
        if(chosenDevice != null)
            bleManager.sendMessageToDevice(advertisement = chosenDevice!!,
                token = token,
                ssid = ssid,
                password = password,
                hash = hash
            )
    }
}