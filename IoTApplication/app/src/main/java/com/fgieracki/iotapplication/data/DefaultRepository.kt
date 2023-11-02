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

    override suspend fun getDevices(): Resource<List<Device>>  {

        val apiDevices = safeApiCall{ api.getDevices(token = USER_TOKEN) }
        if(apiDevices is Resource.Success) {
            val devices = apiDevices.data!!.map { it.toDevice() }
            return Resource.Success(devices)
        }

        return Resource.Error(apiDevices.message!!, apiDevices.code!!)

    }

    override suspend fun addDevice(device: Device): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDevice(device: Device): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDevice(device: Device): Resource<Boolean> {
        TODO("Not yet implemented")
    }

}