package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class StringResponse(
    @SerializedName("message") val message: String,
)
