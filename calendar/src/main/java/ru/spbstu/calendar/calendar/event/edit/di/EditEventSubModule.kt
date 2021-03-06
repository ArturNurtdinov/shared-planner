package ru.spbstu.calendar.calendar.event.edit.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.event.edit.presentation.CreateEventViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class EditEventSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(CreateEventViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return CreateEventViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CreateEventViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(CreateEventViewModel::class.java)
    }
}