package ru.spbstu.calendar.settings.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.presentation.SettingsFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        SettingsSubModule::class,
    ]
)
@ScreenScope
interface SettingsSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): SettingsSubComponent
    }

    fun inject(fragment: SettingsFragment)

}