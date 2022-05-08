package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class GoogleAuthCodeBody(
    @SerializedName("auth_code")
    val code: String,
)