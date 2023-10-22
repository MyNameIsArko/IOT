package com.fgieracki.iotapplication.ui.application.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fgieracki.iotapplication.ui.application.viewModels.AddDeviceViewModel
import com.fgieracki.iotapplication.ui.components.AddDeviceAppBar
import com.fgieracki.iotapplication.ui.theme.IoTBlue
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAddDeviceTutorial(
    viewModel: AddDeviceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            AddDeviceAppBar(
                title = "Add Device",
                onRefresh = { viewModel.updateWifiNetworkList() },
                onBack = { onBackClick() }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                ListItem(
                    headlineText = { Text("Step 1: Turn ON WiFi and Location in your device") },
                    leadingContent = { Text("1/3") },
                    modifier = Modifier.padding(16.dp)
                )
                ListItem(
                    headlineText = { Text("Step 2: Connect to the device's WiFi network") },
                    supportingText = { Text("Device SSID and Password will be provided in the package") },
                    leadingContent = { Text("2/3") },
                    modifier = Modifier.padding(16.dp)
                )

                ListItem(
                    headlineText = { Text("Step 3: Return to the app and click Next") },
                    leadingContent = { Text("3/3") },
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = { onNextClick() },
                    modifier = Modifier
                        .padding(horizontal = 60.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IoTBlue)

                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White)
                }
            }

        }
    )
}