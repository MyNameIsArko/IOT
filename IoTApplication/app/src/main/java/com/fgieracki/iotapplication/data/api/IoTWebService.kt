package com.fgieracki.iotapplication.data.api

import com.fgieracki.iotapplication.data.api.model.DeviceResponse
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

object IoTWebService {
    private const val SERVER_URL = "http://test.com"
    private const val DEVICE_URL = "192.168.4.1"

    val deviceApi: DeviceApi by lazy {
        deviceRetrofit.create(DeviceApi::class.java)
    }

    private val deviceRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(DEVICE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

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
        @POST("api/auth/login")
        suspend fun login(@Body loginData: LoginData): Response<LoginResponse>

        @POST("api/auth/register")
        suspend fun register(@Body loginData: LoginData): Response<StringResponse>

        @POST("api/auth/logout")
        suspend fun logout(@Header("Authorization") token: String): Response<StringResponse>

        @GET("api/Device/list")
        suspend fun getDevices(@Header("Authorization") token: String): Response<List<DeviceResponse>>

        @GET("api/auth/validateToken")
        suspend fun validateToken(@Header("Authorization") token: String): Response<StringResponse>

        @GET("api/Device/LatestDeviceCount")
        suspend fun getLatestDeviceCount(@Header("Authorization") token: String): Response<StringResponse>
    }

    interface DeviceApi {
        @POST("/")
        suspend fun addDevice(@Query("ssid") ssid: String, @Query("password") password: String, @Query("jwt") jwt: String): Response<String>
    }
}