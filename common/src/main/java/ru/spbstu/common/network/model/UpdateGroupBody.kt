package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class UpdateGroupBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("users_ids")
    val userIds: List<Long>,
)