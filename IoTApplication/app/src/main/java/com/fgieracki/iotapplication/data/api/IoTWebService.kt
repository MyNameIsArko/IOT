package com.fgieracki.iotapplication.data.api

import com.fgieracki.iotapplication.data.api.model.DeviceListResponse
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.TokenData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

object IoTWebService {
    private const val SERVER_URL = "http://srv3.enteam.pl:180/"

    val api: IoTApi by lazy {
        retrofit.create(IoTApi::class.java)
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface IoTApi {
        @POST("api/Auth/login")
        suspend fun login(@Body loginData: LoginData): Response<LoginResponse>

        @POST("api/Auth/register")
        suspend fun register(@Body loginData: LoginData): Response<StringResponse>

        @GET("api/Device/list")
        suspend fun getDevices(@Header("Authorization") token: String): Response<DeviceListResponse>

        @POST("api/Device/token")
        suspend fun generateToken(@Header("Authorization") token: String,
                                  @Body tokenData: TokenData): Response<StringResponse>

    }
}