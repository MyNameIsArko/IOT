package com.fgieracki.iotapplication.data.model

data class LoginInputFields(
    val username: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
)
