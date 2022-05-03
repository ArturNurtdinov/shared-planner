package ru.spbstu.sharedplanner.root.di

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.sharedplanner.root.presentation.di.RootActivityComponent
import ru.spbstu.sharedplanner.root.presentation.main.di.MainFragmentComponent
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.sharedplanner.navigation.Navigator

@Component(
    dependencies = [
        RootDependencies::class
    ],
    modules = [
        RootFeatureModule::class,
    ]
)
@FeatureScope
interface RootComponent {

    fun mainActivityComponentFactory(): RootActivityComponent.Factory

    fun mainFragmentComponentFactory(): MainFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance navigator: Navigator,
            deps: RootDependencies
        ): RootComponent
    }

    @Component(
        dependencies = [
            CommonApi::class,
        ]
    )
    interface RootFeatureDependenciesComponent : RootDependencies
}
