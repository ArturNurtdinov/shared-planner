package ru.spbstu.calendar.calendar.event.edit.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.calendar.event.edit.presentation.CreateEventFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        EditEventSubModule::class,
    ]
)
@ScreenScope
interface EditEventSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): EditEventSubComponent
    }

    fun inject(fragment: CreateEventFragment)

}