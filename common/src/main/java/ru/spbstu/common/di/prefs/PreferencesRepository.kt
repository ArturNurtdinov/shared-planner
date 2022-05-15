package ru.spbstu.common.di.prefs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface PreferencesRepository {
    val token: String
    fun setToken(token: String)

    val refresh: String
    fun setRefresh(refresh: String)

    val lastSignedType: LastSignedType?
    fun setLastSignedType(type: LastSignedType)

    val selfId: Long?
    fun setSelfId(selfId: Long)

    val notifsEnabled: Boolean
    fun setNotifsEnabled(value: Boolean)

    fun getNotifsEnabledForGroupId(groupId: Long): Boolean
    fun setNotifsEnabledForGroupId(groupId: Long, value: Boolean)

    fun clearTokens()

    @Parcelize
    public enum class LastSignedType : Parcelable {
        GOOGLE,
        VK,
        OK,
    }
}