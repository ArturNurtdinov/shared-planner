package ru.spbstu.auth.auth.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.auth.AuthRouter
import ru.spbstu.auth.auth.presentation.AuthViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class AuthSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    fun provideViewModel(authRouter: AuthRouter): ViewModel {
        return AuthViewModel(authRouter)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): AuthViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(AuthViewModel::class.java)
    }
}