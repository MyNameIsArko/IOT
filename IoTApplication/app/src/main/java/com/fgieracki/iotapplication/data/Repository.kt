package com.fgieracki.iotapplication.data

import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.model.Device
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    suspend fun login(username: String, password: String): Response<LoginResponse>
    suspend fun register(username: String, password: String): Response<StringResponse>
    fun getDevices(): Flow<List<Device>>
    suspend fun addDevice(device: Device): Boolean
    suspend fun deleteDevice(device: Device): Boolean
    suspend fun updateDevice(device: Device): Boolean
}