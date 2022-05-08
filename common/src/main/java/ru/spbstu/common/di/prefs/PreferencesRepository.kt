package ru.spbstu.common.di.prefs

interface PreferencesRepository {
    val token: String
    fun setToken(token: String)

    val refresh: String
    fun setRefresh(refresh: String)
}