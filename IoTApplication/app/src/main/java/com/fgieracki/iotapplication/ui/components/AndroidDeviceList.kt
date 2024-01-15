package com.fgieracki.iotapplication.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.juul.kable.AndroidAdvertisement

@Composable
fun AndroidDeviceList(
    modifier: Modifier = Modifier,
    devices: List<AndroidAdvertisement>,
    onClick: (AndroidAdvertisement) -> Unit = {}
) {
    if (devices.isEmpty()) {
        Text(
            text = "Location or Bluetooth might be OFF or there are no devices in range.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Red
        )
    } else {
        LazyColumn {
            items(devices) { device ->
                AndroidDeviceItem(device = device, onClick = { onClick(device) })
            }
        }
    }
}