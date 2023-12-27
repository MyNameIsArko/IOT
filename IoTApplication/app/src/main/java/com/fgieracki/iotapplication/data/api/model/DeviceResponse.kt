package com.fgieracki.iotapplication.data.api.model

import android.content.Context
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.di.EncryptionManager
import com.fgieracki.iotapplication.domain.model.Device
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class DeviceResponse(
    @SerializedName("Name") val deviceName: String,
    @SerializedName("LastTemperatureUpdate") val lastTemperatureUpdateTimestamp: Timestamp,
    @SerializedName("LastHumidityUpdate") val lastHumidityUpdateTimestamp: Timestamp,
    @SerializedName("Temperature") val temperature: String,
    @SerializedName("Humidity") val humidity: String,
    @SerializedName("Mac") val mac: String,
)

fun DeviceResponse.toDevice() = Device(
    name = deviceName,
    lastTemperatureUpdateTimestamp = lastTemperatureUpdateTimestamp,
    lastHumidityUpdateTimestamp = lastHumidityUpdateTimestamp,
    temperature = decrypt(temperature, mac),
    humidity = decrypt(humidity, mac),
    mac = mac,
)

private fun getDeviceKey(deviceKey: String): String {
    val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    return sharedPreference.getString(deviceKey, "")?:""
}

private fun decrypt(message: String, deviceKey: String): String {
    val encryptionManager = EncryptionManager()
    return encryptionManager.decrypt(message, getDeviceKey(deviceKey))
}