package ru.spbstu.calendar.domain.model

import ru.spbstu.common.BuildConfig
import ru.spbstu.common.network.model.UserResponse

data class Profile(
    val id: Long,
    val name: String,
    val avatarUrl: String? = null,
    val email: String? = null,
    val phone: String? = null,
) {
    companion object {
        fun fromNetworkModule(userResponse: UserResponse): Profile {
            return Profile(
                userResponse.id,
                userResponse.fullName,
                BuildConfig.ENDPOINT + userResponse.photo,
                userResponse.email,
                userResponse.phone,
            )
        }
    }
}