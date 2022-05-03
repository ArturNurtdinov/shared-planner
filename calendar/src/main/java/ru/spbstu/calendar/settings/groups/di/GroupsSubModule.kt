package ru.spbstu.calendar.settings.groups.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.groups.presentation.GroupsViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class GroupsSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(GroupsViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return GroupsViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): GroupsViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(GroupsViewModel::class.java)
    }
}