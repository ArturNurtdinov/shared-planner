package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenBody(
    @SerializedName("refresh_token")
    val refresh: String,
)