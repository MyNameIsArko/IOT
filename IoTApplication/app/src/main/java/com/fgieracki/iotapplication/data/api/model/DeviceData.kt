package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class DeviceData(
    @SerializedName("mac") val mac: String,
    @SerializedName("key") val key: String,
    @SerializedName("iv") val iv: String
)
