package com.fgieracki.iotapplication.data.model

import java.sql.Timestamp

data class Device(
    val deviceId: Int,
    val lastTemperatureUpdateTimestamp: Timestamp,
    val lastHumidityUpdateTimestamp: Timestamp,
    val temperature: String,
    val humidity: String,
    val mac: String,
    val state: Boolean,
)
