package com.fgieracki.iotapplication.data

import android.content.Context
import com.fgieracki.iotapplication.data.api.IoTWebService
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.toDevice
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.data.model.Device
import com.fgieracki.iotapplication.data.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultRepository : Repository() {
    private val api = IoTWebService.api
    private val deviceApi = IoTWebService.deviceApi

    private var USER_TOKEN = "Token"
    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    private fun getToken() {
        val token: String = sharedPreference.getString("USER_TOKEN", "Token")?:"Token"
        USER_TOKEN = token
    }

    override suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return safeApiCall{ api.login(LoginData(username, password)) }
    }

    override suspend fun register(username: String, password: String): Resource<StringResponse> {
        return safeApiCall{ api.register(LoginData(username, password)) }
    }

    override suspend fun getDevices(): Flow<Resource<List<Device>>> = flow<Resource<List<Device>>>{

        val apiResponse = safeApiCall{ api.getDevices(token = USER_TOKEN) }
        if(apiResponse is Resource.Success) {
            val devices = apiResponse.data!!.map { it.toDevice() }
            emit(Resource.Success(devices))
        }
        else {
            emit(Resource.Error(apiResponse.message!!, apiResponse.code!!))
        }

    }

    override suspend fun addDevice(ssid: String, psswd: String): Resource<String> {
        return safeApiCall{ deviceApi.addDevice(ssid, psswd, USER_TOKEN) }
    }

    override suspend fun deleteDevice(device: Device): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDevice(device: Device): Resource<Boolean> {
        TODO("Not yet implemented")
    }

}