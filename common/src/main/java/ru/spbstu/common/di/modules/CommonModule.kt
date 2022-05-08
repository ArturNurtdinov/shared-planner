package ru.spbstu.common.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Module
import dagger.Provides
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.prefs.PreferencesRepositoryImpl
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.common.errors.ErrorStringsProvider
import javax.inject.Named

const val SHARED_PREFERENCES_FILE = "ru.spbstu.sharedplanner.preferences"
const val ENCRYPTED_SHARED_PREFERENCES_FILE = "ru.spbstu.sharedplanner.encrypted"

@Module
class CommonModule {
    @Provides
    @ApplicationScope
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    @Provides
    @ApplicationScope
    fun providePreferencesRepository(sharedPreferences: SharedPreferences): PreferencesRepository =
        PreferencesRepositoryImpl(sharedPreferences)

    @Provides
    @ApplicationScope
    fun provideErrorStringsProvider(appContext: Context): ErrorStringsProvider =
        ErrorStringsProvider(appContext)
}
