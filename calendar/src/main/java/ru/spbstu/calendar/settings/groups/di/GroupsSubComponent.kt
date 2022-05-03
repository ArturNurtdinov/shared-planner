package ru.spbstu.calendar.settings.groups.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.groups.presentation.GroupsFragment
import ru.spbstu.calendar.settings.presentation.SettingsFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        GroupsSubModule::class,
    ]
)
@ScreenScope
interface GroupsSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): GroupsSubComponent
    }

    fun inject(fragment: GroupsFragment)

}