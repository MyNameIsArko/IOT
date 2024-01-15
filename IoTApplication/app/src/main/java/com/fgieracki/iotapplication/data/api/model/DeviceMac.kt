package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class DeviceMac(
    @SerializedName("mac") val mac: String
)
