package com.fgieracki.iotapplication.data.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FoundDeviceReceiver(
    private val onDeviceFound: (BluetoothDevice) -> Unit
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
                val device: BluetoothDevice? = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java)
                device?.let {
                    onDeviceFound(it)
                }
            }
        }
    }
}