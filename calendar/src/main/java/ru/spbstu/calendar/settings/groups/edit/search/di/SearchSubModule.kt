package ru.spbstu.calendar.settings.groups.edit.search.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.settings.groups.edit.search.SearchViewModel
import ru.spbstu.common.di.viewmodel.ViewModelKey
import ru.spbstu.common.di.viewmodel.ViewModelModule

@Module(
    includes = [
        ViewModelModule::class,
    ]
)
class SearchSubModule {
    @Provides
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun provideViewModel(router: CalendarRouter): ViewModel {
        return SearchViewModel(router)
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): SearchViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(SearchViewModel::class.java)
    }
}