package com.fgieracki.iotapplication.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.domain.model.Device
import com.fgieracki.iotapplication.ui.theme.IoTAmaranth
import com.fgieracki.iotapplication.ui.theme.IoTBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceItem(device: Device, deleteDevice: (Device) -> Unit = {}) {
    val menuExpanded = remember { mutableStateOf(false) }

    ListItem(
        headlineText = { Text(text = device.name) },
        overlineText = { Text(text = "Last update: " + device.lastTemperatureUpdateTimestamp.toString()) },
        supportingText = { Text(text = "Temperature: " + device.temperature + ", humidity: " + device.humidity) },
        trailingContent = {
            IconButton(
                onClick = { menuExpanded.value = !menuExpanded.value }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Device Options",
                    tint = IoTBlue
                )
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_24),
                            contentDescription = "Delete Device",
                            tint = IoTAmaranth
                        )
                    },
                    text = { Text(text = "Delete device") },
                    onClick = { deleteDevice(device) }
                )
            }
        }
    )
}