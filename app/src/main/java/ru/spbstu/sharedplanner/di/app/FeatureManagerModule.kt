package ru.spbstu.sharedplanner.di.app

import dagger.Module
import dagger.Provides
import ru.spbstu.sharedplanner.di.deps.FeatureHolderManager
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.scope.ApplicationScope

@Module
class FeatureManagerModule {

    @ApplicationScope
    @Provides
    fun provideFeatureHolderManager(featureApiHolderMap: @JvmSuppressWildcards Map<Class<*>, FeatureApiHolder>): FeatureHolderManager {
        return FeatureHolderManager(featureApiHolderMap)
    }
}
