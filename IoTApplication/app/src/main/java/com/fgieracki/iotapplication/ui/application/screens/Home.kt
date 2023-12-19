package com.fgieracki.iotapplication.ui.application.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.fgieracki.iotapplication.ui.application.viewModels.HomeViewModel
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

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> { viewModel.removeHandler() }
            Lifecycle.State.INITIALIZED -> { }
            Lifecycle.State.CREATED -> { viewModel.addHandler()}
            Lifecycle.State.STARTED -> { viewModel.addHandler()}
            Lifecycle.State.RESUMED -> { viewModel.addHandler()}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navChannel.collectLatest {
            viewModel.removeHandler()
            if(it == "logout") {
                onLogout()
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastChannel.collectLatest {
            Toast.makeText(context, it,
                Toast.LENGTH_LONG).show()
        }
    }

    val devices = viewModel.devicesState.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            IoTAppBar(title = "IoT Application", onLogout = {
                viewModel.removeHandler()
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
                    viewModel.removeHandler()
                    onAddDevice()
                          },
            )
        }
    )

}