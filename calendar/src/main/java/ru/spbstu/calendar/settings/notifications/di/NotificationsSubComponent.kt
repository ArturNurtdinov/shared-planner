package ru.spbstu.calendar.settings.notifications.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.settings.notifications.presentation.NotificationsFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        NotificationsSubModule::class,
    ]
)
@ScreenScope
interface NotificationsSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): NotificationsSubComponent
    }

    fun inject(fragment: NotificationsFragment)

}