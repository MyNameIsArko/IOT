package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fgieracki.iotapplication.data.model.Device

@Composable
fun DeviceList(devices: List<Device>, modifier: Modifier = Modifier, deleteDevice: (Device) -> Unit = {}) {
    LazyColumn(modifier = modifier) {
        items(devices.size) { index ->
            DeviceItem(device = devices[index], deleteDevice = deleteDevice)
        }
    }
}