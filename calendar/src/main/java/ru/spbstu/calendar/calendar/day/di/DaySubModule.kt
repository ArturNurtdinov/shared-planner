package ru.spbstu.calendar.calendar.day.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.day.presentation.DayViewModel
import ru.spbstu.calendar.settings.presentation.SettingsViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class DaySubModule {
    @Provides
    @IntoMap
    @ViewModelKey(DayViewModel::class)
    fun provideViewModel(
        router: CalendarRouter,
        calendarRepository: CalendarRepository
    ): ViewModel {
        return DayViewModel(router, calendarRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): DayViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(DayViewModel::class.java)
    }
}