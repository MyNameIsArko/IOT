package com.fgieracki.iotapplication.ui.components

import android.net.wifi.ScanResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.fgieracki.iotapplication.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkItem(scanResult: ScanResult, onClick: (ScanResult) -> Unit = {}) {
    ListItem(
        headlineText = { Text(text = "SSID: ${scanResult.wifiSsid}") },
        supportingText = { Text(text = "Signal Strength: ${scanResult.level} dBm") },
        leadingContent = {
            Icon(
                painter = if(scanResult.capabilities.contains("WPA"))
                    painterResource(id = R.drawable.baseline_wifi_password_24)
                else painterResource(id = R.drawable.baseline_wifi_24),
                contentDescription = "Wifi Icon"
            )
        },
        trailingContent = {
            IconButton(
                onClick = { onClick(scanResult) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Icon"
                )
            }
        }
    )
}