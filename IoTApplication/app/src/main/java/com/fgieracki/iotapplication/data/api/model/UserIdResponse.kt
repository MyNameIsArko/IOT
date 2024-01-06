package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class UserIdResponse(
    @SerializedName("userId") val userId: String,
)
