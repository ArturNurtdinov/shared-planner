package ru.spbstu.auth.di

import dagger.Module
import dagger.Provides
import ru.spbstu.auth.AuthRepository
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.common.network.Api

@Module
class AuthDataModule {
    @Provides
    @FeatureScope
    fun provideAuthRepository(
        api: Api,
        preferencesRepository: PreferencesRepository
    ): AuthRepository =
        AuthRepository(api, preferencesRepository)
}