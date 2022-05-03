package ru.spbstu.calendar.settings.groups.edit.search.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.groups.edit.search.SearchFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        SearchSubModule::class,
    ]
)
@ScreenScope
interface SearchSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): SearchSubComponent
    }

    fun inject(fragment: SearchFragment)

}