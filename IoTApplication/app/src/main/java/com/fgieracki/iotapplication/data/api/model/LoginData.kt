package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("userName") val userName: String,
    @SerializedName("password") val password: String
)