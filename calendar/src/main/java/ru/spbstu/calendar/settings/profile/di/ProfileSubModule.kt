package ru.spbstu.calendar.settings.profile.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.profile.presentation.ProfileViewModel
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.errors.ErrorStringsProvider

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class ProfileSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun provideViewModel(
        router: CalendarRouter,
        calendarRepository: CalendarRepository,
        errorStringsProvider: ErrorStringsProvider,
        preferencesRepository: PreferencesRepository,
    ): ViewModel {
        return ProfileViewModel(router, calendarRepository, errorStringsProvider, preferencesRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): ProfileViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(ProfileViewModel::class.java)
    }
}