package ru.spbstu.auth.auth.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.auth.AuthRepository
import ru.spbstu.auth.AuthRouter
import ru.spbstu.auth.auth.presentation.AuthViewModel
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.errors.ErrorStringsProvider

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class AuthSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun provideViewModel(
        authRouter: AuthRouter,
        authRepository: AuthRepository,
        errorStringsProvider: ErrorStringsProvider,
        preferencesRepository: PreferencesRepository,
    ): ViewModel {
        return AuthViewModel(authRouter, authRepository, errorStringsProvider, preferencesRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): AuthViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(AuthViewModel::class.java)
    }
}