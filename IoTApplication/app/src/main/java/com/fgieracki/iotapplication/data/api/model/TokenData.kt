package com.fgieracki.iotapplication.data.api.model

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("Value") val token: String
)
