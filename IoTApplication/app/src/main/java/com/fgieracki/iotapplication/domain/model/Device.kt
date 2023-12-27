package com.fgieracki.iotapplication.domain.model

import java.sql.Timestamp

data class Device(
    val name: String,
    val lastTemperatureUpdateTimestamp: Timestamp,
    val lastHumidityUpdateTimestamp: Timestamp,
    val temperature: String,
    val humidity: String,
    val mac: String,
)
