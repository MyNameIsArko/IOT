package com.fgieracki.iotapplication.ui.application.viewModels

import android.os.Handler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.model.Device
import com.fgieracki.iotapplication.data.model.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository = DefaultRepository()) : ViewModel() {
    var devicesFlow: MutableStateFlow<List<Device>> = MutableStateFlow(emptyList())


    val navChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val _toastChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastChannel = _toastChannel

    lateinit var mainHandler: Handler
    private val updateDataTask = object : Runnable {
        override fun run() {
            updateDevicesState()
            mainHandler.postDelayed(this, 10000)
        }
    }

    fun updateDevicesState() {
        viewModelScope.launch() {
            val resource = repository.getDevices()
            if(resource is Resource.Success) {
                devicesFlow.value = resource.data!!
            }
            else if (resource.code == 401) {
                println("Unauthorized")
                _toastChannel.tryEmit("Session expired")
                navChannel.tryEmit("logout")
            }
        }
    }

    fun deleteDevice(device: Device) {
        println("Delete device: $device")
    }


}