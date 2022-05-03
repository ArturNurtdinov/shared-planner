package ru.spbstu.calendar.calendar.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.calendar.presentation.CalendarFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        CalendarSubModule::class,
    ]
)
@ScreenScope
interface CalendarSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): CalendarSubComponent
    }

    fun inject(fragment: CalendarFragment)

}