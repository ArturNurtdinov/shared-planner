package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("users")
    val users: List<UserResponse>,
    @SerializedName("page")
    val page: Int,
)