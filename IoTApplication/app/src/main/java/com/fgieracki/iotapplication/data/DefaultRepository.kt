package com.fgieracki.iotapplication.data

import android.content.Context
import com.fgieracki.iotapplication.data.api.IoTWebService
import com.fgieracki.iotapplication.data.api.model.DeviceResponse
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.toDevice
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.data.model.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class DefaultRepository : Repository {
    private val api = IoTWebService.api
    private val deviceApi = IoTWebService.deviceApi

    private var USER_TOKEN = "Token"
    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    private fun getToken() {
        val token: String = sharedPreference.getString("USER_TOKEN", "Token")?:"Token"
        USER_TOKEN = token
    }

    override suspend fun login(username: String, password: String): Response<LoginResponse> {

        return api.login(LoginData(username, password))
    }

    override suspend fun register(username: String, password: String): Response<StringResponse> {
        return api.register(LoginData(username, password))
    }

    override fun getDevices(): Flow<List<Device>> = flow<List<Device>> {
        getToken()
        val response = api.getDevices(token = USER_TOKEN)

        if (response.isSuccessful) {
           val devicesResponse: List<DeviceResponse> = response.body()!!
            emit(devicesResponse.map { it.toDevice() })
        }
        else {
            emit(listOf())
        }
    }

    override suspend fun addDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateDevice(device: Device): Boolean {
        TODO("Not yet implemented")
    }

}