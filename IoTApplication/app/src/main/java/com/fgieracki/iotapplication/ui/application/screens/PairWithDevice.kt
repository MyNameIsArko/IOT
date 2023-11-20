package com.fgieracki.iotapplication.ui.application.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.data.model.BluetoothDevice
import com.fgieracki.iotapplication.ui.application.viewModels.BluetoothViewModel
import com.fgieracki.iotapplication.ui.components.AddDeviceAppBar
import com.fgieracki.iotapplication.ui.components.BluetoothDeviceList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPairWithDevice(
    viewModel: BluetoothViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onAddDevice: (BluetoothDevice) -> Unit = {}
) {
    val state = viewModel.state.collectAsState()

    LaunchedEffect(key1 = state.value.errorMessage) {
        state.value.errorMessage?.let { message ->
            Toast.makeText(
                ContextCatcher.getContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    LaunchedEffect(key1 = state.value.isConnected) {
        if (state.value.isConnected) {
            Toast.makeText(
                ContextCatcher.getContext(),
                "Connected to device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when {
        state.value.isConnecting -> {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.CenterHorizontally
                    )
                )
            }
        }
        else -> {
            Scaffold(
                topBar = {
                    AddDeviceAppBar(
                        title = "Pair with device",
                        hideRefresh = true,
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
                            text = "Choose device to pair with from the list below",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        BluetoothDeviceList(
                            devices = state.value.pairedDevices + state.value.scannedDevices,
                            onClick = { device ->
                                viewModel.connectToDevice(device)
                            }
                        )
                    }
                }
            )
        }
    }
}