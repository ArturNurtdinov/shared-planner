package ru.spbstu.calendar.settings.groups.edit.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.groups.edit.presentation.CreateGroupFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        CreateGroupSubModule::class,
    ]
)
@ScreenScope
interface CreateGroupSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): CreateGroupSubComponent
    }

    fun inject(fragment: CreateGroupFragment)

}