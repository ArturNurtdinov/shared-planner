package ru.spbstu.auth.auth.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import ru.spbstu.auth.auth.presentation.AuthFragment
import ru.spbstu.common.di.scope.ScreenScope

@Subcomponent(
    modules = [
        AuthSubModule::class,
    ]
)
@ScreenScope
interface AuthSubComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): AuthSubComponent
    }

    fun inject(authFragment: AuthFragment)

}