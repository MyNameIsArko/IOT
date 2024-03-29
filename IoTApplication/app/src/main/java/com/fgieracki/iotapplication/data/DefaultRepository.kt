package com.fgieracki.iotapplication.data

import android.content.Context
import com.fgieracki.iotapplication.data.api.IoTWebService
import com.fgieracki.iotapplication.data.api.model.DeviceMac
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.TokenData
import com.fgieracki.iotapplication.data.api.model.UserIdResponse
import com.fgieracki.iotapplication.data.api.model.toDevice
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.domain.model.Device
import com.fgieracki.iotapplication.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DefaultRepository : Repository() {
    private val api = IoTWebService.api

    private var USER_TOKEN = "Token"
    private val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

    private fun updateToken() {
        val token: String = sharedPreference.getString("USER_TOKEN", "Token")?:"Token"
        USER_TOKEN = token
    }

    private fun getToken(): String {
        return sharedPreference.getString("USER_TOKEN", "Token")?:"Token"
    }

    override suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return safeApiCall{ api.login(LoginData(username, password)) }
    }

    override suspend fun register(username: String, password: String): Resource<StringResponse> {
        return safeApiCall{ api.register(LoginData(username, password)) }
    }

    override suspend fun getDevices(): Flow<Resource<List<Device>>> = flow<Resource<List<Device>>>{
        val apiResponse = safeApiCall{ api.getDevices(token = getToken()) }
        if(apiResponse is Resource.Success) {
            val devices = apiResponse.data!!.devices.map { it.toDevice() }
            emit(Resource.Success(devices))
        }
        else {
            emit(Resource.Error(apiResponse.message!!, apiResponse.code!!))
        }
    }

    override suspend fun generateToken(tokenData: TokenData): Resource<UserIdResponse> {
        return safeApiCall{ api.generateToken(getToken(), tokenData) }
    }

    override suspend fun deleteDevice(device: Device): Resource<StringResponse> {
        return safeApiCall { api.deleteDevice(getToken(), DeviceMac(device.mac)) }
    }

    override suspend fun getDevicesCount(): Resource<Long> {
        val apiResponse = safeApiCall{ api.getDevices(token = getToken()) }
        if(apiResponse is Resource.Success) {
            val devices = apiResponse.data!!.devices.map { it.toDevice() }
            return Resource.Success(devices.size.toLong())
        }
        else {
            return Resource.Error(apiResponse.message!!, apiResponse.code!!)
        }
    }


}