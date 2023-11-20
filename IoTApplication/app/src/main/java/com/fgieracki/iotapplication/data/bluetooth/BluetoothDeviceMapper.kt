package com.fgieracki.iotapplication.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.fgieracki.iotapplication.data.model.BluetoothDeviceDomain


@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address,
    )
}