package com.fgieracki.iotapplication.ui.application.viewModels

import com.fgieracki.iotapplication.data.model.BluetoothDevice

data class BluetoothUiState (
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null
)