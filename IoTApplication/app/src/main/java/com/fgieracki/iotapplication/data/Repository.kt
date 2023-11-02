package com.fgieracki.iotapplication.data

import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.model.Device
import com.fgieracki.iotapplication.data.model.Resource

abstract class Repository : BaseRepository() {
    abstract suspend fun login(username: String, password: String): Resource<LoginResponse>
    abstract suspend fun register(username: String, password: String): Resource<StringResponse>
    abstract suspend fun getDevices(): Resource<List<Device>>
    abstract suspend fun addDevice(device: Device): Resource<Boolean>
    abstract suspend fun deleteDevice(device: Device): Resource<Boolean>
    abstract suspend fun updateDevice(device: Device): Resource<Boolean>
}