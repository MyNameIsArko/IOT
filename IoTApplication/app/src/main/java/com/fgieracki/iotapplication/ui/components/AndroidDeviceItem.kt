package com.fgieracki.iotapplication.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.fgieracki.iotapplication.R
import com.juul.kable.AndroidAdvertisement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidDeviceItem(device: AndroidAdvertisement, onClick: (AndroidAdvertisement) -> Unit = {}) {
    ListItem(
        headlineText = { Text(text = "Name: ${device.name}") },
        supportingText = { Text(text = "Address: ${device.address}") },
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