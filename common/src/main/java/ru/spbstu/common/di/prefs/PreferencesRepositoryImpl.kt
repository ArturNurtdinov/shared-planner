package ru.spbstu.common.di.prefs

import android.content.SharedPreferences

internal class PreferencesRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    PreferencesRepository {
    override val token: String
        get() = sharedPreferences.getString(ACCESS_TOKEN_KEY, "") ?: ""

    override fun setToken(token: String) {
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    override val refresh: String
        get() = sharedPreferences.getString(REFRESH_TOKEN_KEY, "") ?: ""

    override fun setRefresh(refresh: String) {
        sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, token).apply()
    }

    override val lastSignedType: PreferencesRepository.LastSignedType?
        get() = kotlin.runCatching {
            PreferencesRepository.LastSignedType.valueOf(
                sharedPreferences.getString(
                    LAST_SIGNED_TYPE,
                    null
                )!!
            )
        }.getOrNull()

    override fun setLastSignedType(type: PreferencesRepository.LastSignedType) {
        sharedPreferences.edit().putString(LAST_SIGNED_TYPE, type.name).apply()
    }

    override fun clearTokens() {
        setRefresh("")
        setToken("")
    }

    private companion object {
        private const val ACCESS_TOKEN_KEY = "ru.spbstu.sharedplanner.prefs.ACCESS_TOKEN_KEY"
        private const val REFRESH_TOKEN_KEY = "ru.spbstu.sharedplanner.prefs.REFRESH_TOKEN_KEY"
        private const val LAST_SIGNED_TYPE = "ru.spbstu.sharedplanner.prefs.LAST_SIGNED_TYPE"
        private const val AUTH_CODE_KEY = "ru.spbstu.sharedplanner.prefs.AUTH_CODE_KEY"
    }
}