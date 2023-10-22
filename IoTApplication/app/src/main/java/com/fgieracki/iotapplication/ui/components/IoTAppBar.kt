package com.fgieracki.iotapplication.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.fgieracki.iotapplication.R
import com.fgieracki.iotapplication.ui.theme.IoTBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoTAppBar(title: String = "IoT Application", onLogout: () -> Unit) {
    val menuExpanded = remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        },
        actions = {
            IconButton(onClick = { menuExpanded.value = !menuExpanded.value }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = "User Options",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = false }) {
                DropdownMenuItem(
                    text = { Text(text = "Logout") },
                    onClick = { onLogout() })
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = IoTBlue
        )
    )
}