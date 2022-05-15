package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class PushTokenBody(
    @SerializedName("push_token")
    val token: String,
)