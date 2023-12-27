package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class DeviceListResponse(
    @SerializedName("deviceList") val devices: List<DeviceResponse>
)
