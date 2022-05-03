package ru.spbstu.calendar.settings.notifications.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.notifications.presentation.NotificationsViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class NotificationsSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return NotificationsViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): NotificationsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(NotificationsViewModel::class.java)
    }
}