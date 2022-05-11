package ru.spbstu.calendar.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.network.model.UserResponse

@Parcelize
data class Profile(
    val id: Long,
    val name: String,
    val avatarUrl: String? = null,
    val email: String? = null,
    val phone: String? = null,
) : Parcelable {
    companion object {
        fun fromNetworkModel(userResponse: UserResponse): Profile {
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