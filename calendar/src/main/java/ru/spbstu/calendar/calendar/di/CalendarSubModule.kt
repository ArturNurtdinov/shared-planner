package ru.spbstu.calendar.calendar.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.presentation.CalendarViewModel
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class CalendarSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    fun provideViewModel(
        router: CalendarRouter,
        calendarRepository: CalendarRepository,
        preferencesRepository: PreferencesRepository,
    ): ViewModel {
        return CalendarViewModel(router, calendarRepository, preferencesRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CalendarViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(CalendarViewModel::class.java)
    }
}