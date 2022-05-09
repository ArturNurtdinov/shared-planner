package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class GroupResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("user_count")
    val userCount: Int,
)