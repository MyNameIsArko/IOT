package com.fgieracki.iotapplication.di.viewModels

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.local.ActivityCatcher
import com.fgieracki.iotapplication.domain.model.Device
import com.fgieracki.iotapplication.domain.model.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class HomeViewModel(private val repository: Repository = DefaultRepository()) : ViewModel() {
    var devicesState: MutableStateFlow<List<Device>> = MutableStateFlow(emptyList())

    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel

    val updateDevicesFlow = flow<Unit> {
        while(coroutineContext.isActive) {
            updateDevicesState()
            kotlinx.coroutines.delay(10000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Unit)

    fun updateDevicesState() {
        viewModelScope.launch {
            val resourceFlow = repository.getDevices()
            Log.d("HomeViewModel", "updateDevicesState")
            val resource = resourceFlow.collect {
                if(it is Resource.Error) {
                    Log.e("HomeViewModel", "Error: ${it.code}: ${it.message}")
                    if(it.code == 401) {
                        navChannel.emit("logout")
                    }
                    else {
                        _toastChannel.emit("Error ${it.code}: ${it.message}")
                    }
                } else {
                    devicesState.value = it.data!!
                    _toastChannel.emit("Devices data updated")
                }
            }

        }
    }

    fun deleteDevice(device: Device) {
        println("Delete device: $device")
        viewModelScope.launch {
            val response = repository.deleteDevice(device);
            if(response is Resource.Error) {
                _toastChannel.emit("Error ${response.code}: ${response.message}")
            } else {
                _toastChannel.emit("Device deleted")
            }
        }
    }

    fun requestAllPermissions(): Boolean {

        ActivityCompat.requestPermissions(
            ActivityCatcher.getActivity(),
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ), 1
        )


        if(ActivityCompat.checkSelfPermission(
                ActivityCatcher.getActivity(),
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _toastChannel.tryEmit("Please allow bluetooth to continue")
            return false
        }

        else if(ActivityCompat.checkSelfPermission(
                ActivityCatcher.getActivity(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _toastChannel.tryEmit("Please allow bluetooth to continue")
            return false
        }

        if(ActivityCompat.checkSelfPermission(
                ActivityCatcher.getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _toastChannel.tryEmit("Please allow location permission to continue")
            return false
        }

        else if(ActivityCompat.checkSelfPermission(
                ActivityCatcher.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _toastChannel.tryEmit("Please allow location permission to continue")
            return false
        }

        return true
    }
}