package ru.spbstu.common.errors

import android.content.Context
import ru.spbstu.common.R

class ErrorStringsProvider(private val appContext: Context) {
    fun provideAuthErrorString(): String = appContext.getString(R.string.auth_error)
    fun provideDataLoadingError(): String = appContext.getString(R.string.data_error)
}