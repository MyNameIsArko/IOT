package com.fgieracki.iotapplication.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.data.model.BluetoothDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit = {},
) {
    ListItem(
        headlineText = { Text(text = "Name: ${device.name}") },
        supportingText = { Text(text = "MAC: ${device.address}") },
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_bluetooth_24),
                contentDescription = "Wifi Icon"
            )
        },
        trailingContent = {
            IconButton(
                onClick = { onClick(device) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Icon"
                )
            }
        }
    )
}