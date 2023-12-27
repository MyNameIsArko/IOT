package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("UserName") val userName: String,
    @SerializedName("Password") val password: String
)