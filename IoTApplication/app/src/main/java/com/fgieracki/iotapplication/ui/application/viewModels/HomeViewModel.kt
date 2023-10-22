package com.fgieracki.iotapplication.ui.application.viewModels

import androidx.lifecycle.ViewModel
import com.fgieracki.iotapplication.data.model.Device
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel() : ViewModel() {
    val _devices = listOf<Device>(
        Device(1, "Device1", true, "TEMPERATURE", "15"),
        Device(2, "Device2", false, "TEMPERATURE", "15"),
        Device(3, "Device3", true, "TEMPERATURE", "15"),
        Device(4, "Device4", false, "TEMPERATURE", "15"),
        Device(5, "Device5", true, "TEMPERATURE", "15"),
    )

    val devices: MutableStateFlow<List<Device>> = MutableStateFlow(_devices)

    fun deleteDevice(device: Device) {
        println("Delete device: $device")
        devices.value = devices.value.filter { it.id != device.id }

    }

    fun addDevice() {}

}