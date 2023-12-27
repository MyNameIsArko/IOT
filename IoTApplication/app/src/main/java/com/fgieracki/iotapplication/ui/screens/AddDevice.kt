package com.fgieracki.iotapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fgieracki.iotapplication.di.viewModels.AddDeviceViewModel
import com.fgieracki.iotapplication.ui.components.AddDeviceAppBar
import com.fgieracki.iotapplication.ui.components.AddDeviceDialog
import com.fgieracki.iotapplication.ui.components.NetworkList
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenAddDevice(
    viewModel: AddDeviceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {}
) {
    val scanResults = viewModel.wifiNetworkListState.collectAsState(initial = emptyList())
    val addDeviceDialogState = remember { mutableStateOf(false) }
    val ssid = viewModel.ssid.collectAsState(initial = "")
    val password = viewModel.password.collectAsState(initial = "")

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            if(it == "deviceList"){
                onBackClick()
            }
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
                    Text(
                        text = "Choose device Wi-Fi network from the list below.",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    NetworkList(
                        scanResults = scanResults.value,
                        onClick = {
                            viewModel.selectNetwork(it)
                            addDeviceDialogState.value = true
                        }
                    )
            }

            if(addDeviceDialogState.value) {
                AddDeviceDialog(
                    onDismiss = { addDeviceDialogState.value = false },
                    onAddDevice = {
                        viewModel.connectDevice()
                        addDeviceDialogState.value = false
                    },
                    ssid = ssid.value,
                    password = password.value,
                    onPasswordChange = { viewModel.onPasswordChange(it) }
                )
            }

        }
    )

}