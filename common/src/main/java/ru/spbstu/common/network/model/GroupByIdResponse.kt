package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class GroupByIdResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("creator_id")
    val creatorId: Long,
    @SerializedName("users")
    val users: List<UserResponse>
)