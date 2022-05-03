package ru.spbstu.sharedplanner.root.presentation.main.di

import androidx.fragment.app.FragmentActivity
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.common.di.scope.ScreenScope
import ru.spbstu.sharedplanner.root.presentation.main.MainFragment

@Subcomponent(
    modules = [
        MainFragmentModule::class
    ]
)
@ScreenScope
interface MainFragmentComponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(
            @BindsInstance activity: FragmentActivity
        ): MainFragmentComponent
    }

    fun inject(fragment: MainFragment)
}
