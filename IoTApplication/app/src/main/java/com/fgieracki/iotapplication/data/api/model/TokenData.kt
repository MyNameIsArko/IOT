package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("Value") val token: String,
    @SerializedName("key") val key: String,
    @SerializedName("iv") val iv: String
)
