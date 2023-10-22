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

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    IoTApplicationTheme {
//        DeviceList(devices =
//            listOf<Device>(
//                Device(1, "Device1", true, "TEMPERATURE", "15"),
//                Device(2, "Device2", false, "TEMPERATURE", "15"),
//                Device(3, "Device3", true, "TEMPERATURE", "15"),
//                Device(4, "Device4", false, "TEMPERATURE", "15"),
//                Device(5, "Device5", true, "TEMPERATURE", "15"),
//            )
//        )
//    }
//}