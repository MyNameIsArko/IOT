package com.fgieracki.iotapplication.domain.model

import java.util.Date

data class Device(
    val name: String,
    val lastTemperatureUpdateTimestamp: Date,
    val lastHumidityUpdateTimestamp: Date,
    val temperature: String,
    val humidity: String,
    val mac: String,
)
