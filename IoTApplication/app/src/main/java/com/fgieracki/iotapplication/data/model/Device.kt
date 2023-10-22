package com.fgieracki.iotapplication.data.model

data class Device(
    val id: Int,
    val name: String,
    val status: Boolean,
    val type: String,
    val value: String
)
