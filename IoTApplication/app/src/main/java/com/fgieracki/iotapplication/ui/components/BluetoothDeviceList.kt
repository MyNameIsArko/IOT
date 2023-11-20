package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fgieracki.iotapplication.data.model.BluetoothDevice

@Composable
fun BluetoothDeviceList(
    modifier: Modifier = Modifier,
    devices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit = {}
) {
    if (devices.isEmpty()) {
        Text(
            text = "Turn ON Bluetooth to see available devies.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Red
        )
    } else {
        LazyColumn {
            items(devices) { device ->
                BluetoothDeviceItem(device = device, onClick = { onClick(device) })
            }
        }
    }
}