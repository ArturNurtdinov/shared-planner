package ru.spbstu.calendar.settings.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.presentation.SettingsViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class SettingsSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return SettingsViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): SettingsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(SettingsViewModel::class.java)
    }
}