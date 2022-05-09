package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class GroupBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("users_ids")
    val usersIds: List<Long>,
)