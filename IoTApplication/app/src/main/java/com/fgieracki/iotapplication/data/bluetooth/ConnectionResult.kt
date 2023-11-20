package com.fgieracki.iotapplication.data.bluetooth

sealed interface ConnectionResult {
    object ConnectionExtablished: ConnectionResult
    data class Error(val message: String): ConnectionResult

}