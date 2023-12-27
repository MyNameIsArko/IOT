package com.fgieracki.iotapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fgieracki.iotapplication.di.viewModels.HomeViewModel
import com.fgieracki.iotapplication.ui.components.DeviceList
import com.fgieracki.iotapplication.ui.components.FloatingActionButtonAdd
import com.fgieracki.iotapplication.ui.components.IoTAppBar
import kotlinx.coroutines.flow.collectLatest

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenHome(viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
               onLogout: () -> Unit = {},
               onAddDevice: () -> Unit = {},
) {
    viewModel.updateDevicesFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            if(it == "logout") {
                onLogout()
            }
            else
                if(it == "addDevice") {
                onAddDevice()
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_SHORT).show()
        }
    }

    val devices = viewModel.devicesState.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            IoTAppBar(title = "IoT Application", onLogout = {
                onLogout()
            })
        },
        content = {
            DeviceList(
                devices.value,
                modifier = Modifier.padding(it),
                deleteDevice = { viewModel.deleteDevice(it) }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButtonAdd(
                contentDesc = "Add device",
                onClick = {
                    if(viewModel.requestAllPermissions())
                        onAddDevice()
                    },
            )
        }
    )

}