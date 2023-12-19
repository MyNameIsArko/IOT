package com.fgieracki.iotapplication.ui.application.viewModels

import android.os.Handler
import android.util.Log
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
    var devicesState: MutableStateFlow<List<Device>> = MutableStateFlow(emptyList())


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

    init {
        updateDevicesState()
//        mainHandler = Handler()
//        mainHandler.post(updateDataTask)
    }

    fun addHandler() {
        mainHandler = Handler()
        mainHandler.post(updateDataTask)
    }

    fun removeHandler() {
        mainHandler.removeCallbacks(updateDataTask)
    }

    fun updateDevicesState() {
        viewModelScope.launch {
            val resourceFlow = repository.getDevices()
            Log.d("HomeViewModel", "updateDevicesState")
            val resource = resourceFlow.collect {
                if(it is Resource.Error) {
                    if(it.code == 401) {
                        mainHandler.removeCallbacks(updateDataTask)
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
    }


}