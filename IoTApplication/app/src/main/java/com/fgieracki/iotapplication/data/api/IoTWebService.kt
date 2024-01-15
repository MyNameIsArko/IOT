package com.fgieracki.iotapplication.data.api

import com.fgieracki.iotapplication.data.api.model.DeviceListResponse
import com.fgieracki.iotapplication.data.api.model.DeviceMac
import com.fgieracki.iotapplication.data.api.model.LoginData
import com.fgieracki.iotapplication.data.api.model.LoginResponse
import com.fgieracki.iotapplication.data.api.model.StringResponse
import com.fgieracki.iotapplication.data.api.model.TokenData
import com.fgieracki.iotapplication.data.api.model.UserIdResponse
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST

object IoTWebService {
    private const val SERVER_URL = "https://srv9cf.enteam.pl:443/"

    val api: IoTApi by lazy {
        retrofit.create(IoTApi::class.java)
    }

    private val retrofit: Retrofit by lazy {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
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
                                  @Body tokenData: TokenData): Response<UserIdResponse>

        @HTTP(method = "DELETE", path ="api/Device/remove", hasBody = true)
        suspend fun deleteDevice(@Header("Authorization") token: String,
                                 @Body deviceMac: DeviceMac): Response<StringResponse>


    }
}