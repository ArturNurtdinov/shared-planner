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

    private companion object {
        private const val ACCESS_TOKEN_KEY = "ru.spbstu.sharedplanner.prefs.ACCESS_TOKEN_KEY"
        private const val REFRESH_TOKEN_KEY = "ru.spbstu.sharedplanner.prefs.REFRESH_TOKEN_KEY"
    }
}