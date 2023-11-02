package com.fgieracki.iotapplication.data.model

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data, code = 200)
    class Error<T>(errorMessage: String, code: Int) : Resource<T>(message = errorMessage, code = code)
    class Loading<T> : Resource<T>()
}