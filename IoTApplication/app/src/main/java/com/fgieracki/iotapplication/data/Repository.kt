package com.fgieracki.iotapplication.data

import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.TokenData
import com.fgieracki.iotapplication.domain.model.Device
import com.fgieracki.iotapplication.domain.model.Resource
import kotlinx.coroutines.flow.Flow

abstract class Repository : BaseRepository() {
    abstract suspend fun login(username: String, password: String): Resource<LoginResponse>
    abstract suspend fun register(username: String, password: String): Resource<StringResponse>
    abstract suspend fun getDevices(): Flow<Resource<List<Device>>>
//    abstract suspend fun addDevice(ssid: String, psswd: String): Resource<String>
    abstract suspend fun deleteDevice(device: Device): Resource<Boolean>
    abstract suspend fun updateDevice(device: Device): Resource<Boolean>
    abstract suspend fun generateToken(tokenData: TokenData): Resource<StringResponse>
}