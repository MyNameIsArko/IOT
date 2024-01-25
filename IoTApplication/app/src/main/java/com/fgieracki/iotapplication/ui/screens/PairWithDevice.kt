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
import com.fgieracki.iotapplication.di.viewModels.PairDeviceViewModel
import com.fgieracki.iotapplication.ui.components.AddDeviceAppBar
import com.fgieracki.iotapplication.ui.components.AddDeviceDialog
import com.fgieracki.iotapplication.ui.components.AndroidDeviceList
import com.fgieracki.iotapplication.ui.components.LoadingDialog
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPairWithDevice(
    viewModel: PairDeviceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {

    val devicesState = viewModel.scannedDevices.collectAsState()
    val ssid = viewModel.ssid.collectAsState(initial = "")
    val password = viewModel.password.collectAsState(initial = "")
    val addDeviceDialogState = remember { mutableStateOf(false) }
    val isLoading = viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            if(it == "LOGOUT") onLogout()
            if(it == "BACK") onBackClick()
        }
    }

    Scaffold(
        topBar = {
            AddDeviceAppBar(
                title = "Pair with device",
                onRefresh = { viewModel.getScannedDevices() },
                onBack = { onBackClick() }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                if(isLoading.value) {
                    LoadingDialog()
                }

                Text(
                    text = "Choose device to pair with from the list below",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AndroidDeviceList(devices = devicesState.value,
                    onClick = {
                        viewModel.onDeviceClick(it)
                        addDeviceDialogState.value = true
                    })

                if(addDeviceDialogState.value) {
                    AddDeviceDialog(
                        onDismiss = { addDeviceDialogState.value = false },
                        onAddDevice = {
                            viewModel.onPairWithDevice()
                            addDeviceDialogState.value = false
                        },
                        ssid = ssid.value,
                        password = password.value,
                        onPasswordChange = { viewModel.onPasswordChange(it) },
                        onSsidChange = { viewModel.onSsidChange(it) }
                    )
                }
            }
        }
    )
}