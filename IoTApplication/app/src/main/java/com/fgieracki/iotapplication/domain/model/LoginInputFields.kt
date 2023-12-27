package com.fgieracki.iotapplication.domain.model

data class LoginInputFields(
    val username: String = "",
    val password: String = "",
    val passwordRepeat: String = "",
)
