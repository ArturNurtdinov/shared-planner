package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class AttachResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("path")
    val path: String,
)