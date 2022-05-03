package ru.spbstu.calendar.settings.profile.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.di.SettingsSubModule
import ru.spbstu.calendar.settings.profile.presentation.ProfileFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        ProfileSubModule::class,
    ]
)
@ScreenScope
interface ProfileSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): ProfileSubComponent
    }

    fun inject(fragment: ProfileFragment)

}