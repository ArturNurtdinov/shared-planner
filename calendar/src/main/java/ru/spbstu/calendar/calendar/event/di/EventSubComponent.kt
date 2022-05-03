package ru.spbstu.calendar.calendar.event.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.calendar.event.presentation.EventFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        EventSubModule::class,
    ]
)
@ScreenScope
interface EventSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): EventSubComponent
    }

    fun inject(fragment: EventFragment)

}