package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String
)
