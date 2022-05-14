package ru.spbstu.calendar.calendar.event.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.event.presentation.EventViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class EventSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(EventViewModel::class)
    fun provideViewModel(
        router: CalendarRouter,
        calendarRepository: CalendarRepository
    ): ViewModel {
        return EventViewModel(router, calendarRepository)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): EventViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(EventViewModel::class.java)
    }
}