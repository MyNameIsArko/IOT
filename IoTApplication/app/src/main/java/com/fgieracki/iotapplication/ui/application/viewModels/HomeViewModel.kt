package com.fgieracki.iotapplication.ui.application.viewModels

import androidx.lifecycle.ViewModel
import com.fgieracki.iotapplication.data.DefaultRepository
import com.fgieracki.iotapplication.data.Repository
import com.fgieracki.iotapplication.data.model.Device
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: Repository = DefaultRepository()) : ViewModel() {
    val devicesState: Flow<List<Device>> = repository.getDevices()

    fun deleteDevice(device: Device) {
        println("Delete device: $device")
    }


}