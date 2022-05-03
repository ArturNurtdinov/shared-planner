package ru.spbstu.calendar.calendar.day.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.calendar.calendar.day.presentation.DayFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        DaySubModule::class,
    ]
)
@ScreenScope
interface DaySubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): DaySubComponent
    }

    fun inject(fragment: DayFragment)

}