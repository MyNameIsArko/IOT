package com.fgieracki.iotapplication.ui.components

import android.net.wifi.ScanResult
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun NetworkList(
    modifier: Modifier = Modifier,
    scanResults: List<ScanResult>,
    onClick: (ScanResult) -> Unit = {}
) {
    if (scanResults.isEmpty()) {
        Text(
            text = "Turn ON Location to see available WiFi Networks.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Red
        )
    } else {
        LazyColumn {
            items(scanResults) { scanResult ->
                NetworkItem(scanResult = scanResult, onClick = { onClick(scanResult) })
            }
        }
    }
}