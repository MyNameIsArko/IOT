package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class DeviceResponse(
    @SerializedName("deviceId") val deviceId: Int,
    @SerializedName("lastTemperatureUpdate") val lastTemperatureUpdateTimestamp: Timestamp,
    @SerializedName("lastHumidityUpdate") val lastHumidityUpdateTimestamp: Timestamp,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("humidity") val humidity: String,
    @SerializedName("mac") val mac: String,
    @SerializedName("state") val state: Boolean,
)

fun DeviceResponse.toDevice() = com.fgieracki.iotapplication.data.model.Device(
    deviceId = deviceId,
    lastTemperatureUpdateTimestamp = lastTemperatureUpdateTimestamp,
    lastHumidityUpdateTimestamp = lastHumidityUpdateTimestamp,
    temperature = temperature,
    humidity = humidity,
    mac = mac,
    state = state,
)