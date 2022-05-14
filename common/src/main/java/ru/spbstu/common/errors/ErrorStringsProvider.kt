package ru.spbstu.common.errors

import android.content.Context
import ru.spbstu.common.R

class ErrorStringsProvider(val appContext: Context) {
    fun provideAuthErrorString(): String = appContext.getString(R.string.auth_error)
    fun provideDataLoadingError(): String = appContext.getString(R.string.data_error)
    fun provideFileSizeError(): String = appContext.getString(R.string.file_error)
    fun provideNoGroupSelectedError(): String = appContext.getString(R.string.no_group_selected)
    fun provideWrongTimeError(): String = appContext.getString(R.string.wrong_time)
    fun provideActionError(): String = appContext.getString(R.string.action_error)
}