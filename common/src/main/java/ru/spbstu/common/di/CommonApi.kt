package ru.spbstu.common.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import ru.spbstu.common.network.Api

interface CommonApi {
    fun context(): Context

    fun provideSharedPreferences(): SharedPreferences

    fun contentResolver(): ContentResolver

    fun provideApi(): Api
}
