package ru.spbstu.calendar.settings.groups.edit.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.groups.edit.presentation.CreateGroupViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class CreateGroupSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(CreateGroupViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return CreateGroupViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): CreateGroupViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(CreateGroupViewModel::class.java)
    }
}