package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class UpdateGroupSettingsBody(
    @SerializedName("color")
    val color: String,
    @SerializedName("notify")
    val notify: Boolean,
)