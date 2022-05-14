package ru.spbstu.calendar.calendar.event.edit.di

import android.content.ContentResolver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.event.edit.presentation.CreateEventViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule
import ru.spbstu.common.errors.ErrorStringsProvider

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class EditEventSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(CreateEventViewModel::class)
    fun provideViewModel(
        router: CalendarRouter,
        calendarRepository: CalendarRepository,
        contentResolver: ContentResolver,
        errorStringsProvider: ErrorStringsProvider,
    ): ViewModel {
        return CreateEventViewModel(
            router,
            calendarRepository,
            contentResolver,
            errorStringsProvider
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CreateEventViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(CreateEventViewModel::class.java)
    }
}