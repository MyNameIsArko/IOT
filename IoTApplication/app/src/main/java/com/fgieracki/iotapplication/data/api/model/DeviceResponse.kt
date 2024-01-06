package com.fgieracki.iotapplication.data.api.model

import android.content.Context
import com.fgieracki.iotapplication.data.local.ContextCatcher
import com.fgieracki.iotapplication.di.EncryptionManager
import com.fgieracki.iotapplication.domain.model.Device
import com.google.gson.annotations.SerializedName
import java.util.Date

data class DeviceResponse(
    @SerializedName("mac") val mac: String,
    @SerializedName("name") val deviceName: String,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("humidity") val humidity: String,
    @SerializedName("lastTemperatureUpdate") val lastTemperatureUpdateTimestamp: Date,
    @SerializedName("lastHumidityUpdate") val lastHumidityUpdateTimestamp: Date,
)

fun DeviceResponse.toDevice() = Device(
    name = deviceName,
    lastTemperatureUpdateTimestamp = lastTemperatureUpdateTimestamp,
    lastHumidityUpdateTimestamp = lastHumidityUpdateTimestamp,
    temperature = decrypt(temperature, mac),
    humidity = decrypt(humidity, mac),
    mac = mac,
)

private fun getAesKey(deviceKey: String): String {
    val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    return sharedPreference.getString(deviceKey+ "AESKEY", "")?:""
}

private fun getAesIV(deviceKey: String): String {
    val sharedPreference =  ContextCatcher.getContext().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
    return sharedPreference.getString(deviceKey+ "AESIV", "")?:""
}

private fun decrypt(message: String, deviceKey: String): String {
    val encryptionManager = EncryptionManager()
    return encryptionManager.decrypt(message, getAesKey(deviceKey), getAesIV(deviceKey))
}