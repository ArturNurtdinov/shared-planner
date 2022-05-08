package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class TokensResponseBody(
    @SerializedName("access_token")
    val access: String,
    @SerializedName("refresh_token")
    val refresh: String,
)