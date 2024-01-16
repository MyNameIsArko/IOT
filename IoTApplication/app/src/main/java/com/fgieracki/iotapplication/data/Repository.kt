package com.fgieracki.iotapplication.data

import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.TokenData
import com.fgieracki.iotapplication.data.api.model.UserIdResponse
import com.fgieracki.iotapplication.domain.model.Device
import com.fgieracki.iotapplication.domain.model.Resource
import kotlinx.coroutines.flow.Flow

abstract class Repository : BaseRepository() {
    abstract suspend fun login(username: String, password: String): Resource<LoginResponse>
    abstract suspend fun register(username: String, password: String): Resource<StringResponse>
    abstract suspend fun getDevices(): Flow<Resource<List<Device>>>
    abstract suspend fun deleteDevice(device: Device): Resource<StringResponse>
    abstract suspend fun generateToken(tokenData: TokenData): Resource<UserIdResponse>
    abstract suspend fun getDevicesCount(): Resource<Long>
}