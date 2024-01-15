package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("devicesKeys") val deviceKeys: List<DeviceData>,
    @SerializedName("message") val message: String
)
