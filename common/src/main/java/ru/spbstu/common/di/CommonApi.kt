package ru.spbstu.common.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.PictureUrlHelper

interface CommonApi {
    fun context(): Context

    fun providePreferencesRepository(): PreferencesRepository

    fun contentResolver(): ContentResolver

    fun provideApi(): Api

    fun errorStringsProvider(): ErrorStringsProvider

    fun pictureUrlHelper(): PictureUrlHelper
}
