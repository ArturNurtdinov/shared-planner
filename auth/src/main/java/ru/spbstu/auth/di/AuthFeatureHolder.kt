package ru.spbstu.auth.di

import ru.spbstu.auth.AuthRouter
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AuthFeatureHolder @Inject constructor(
    featureContainer: FeatureContainer,
    private val featureRouter: AuthRouter
) : FeatureApiHolder(featureContainer) {
    override fun initializeDependencies(): Any {
        val deps = DaggerAuthComponent_AuthDependenciesComponent.builder()
            .commonApi(commonApi())
            .build()
        return DaggerAuthComponent.factory()
            .create(featureRouter, deps)
    }
}