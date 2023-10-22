package com.fgieracki.iotapplication.data.model

data class User(
    val username: String,
    val id: Int,
    val apiToken: String,
    val newDeviceToken: String,
)
