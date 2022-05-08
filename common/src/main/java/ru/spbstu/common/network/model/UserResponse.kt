package ru.spbstu.common.network.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone_number")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
)