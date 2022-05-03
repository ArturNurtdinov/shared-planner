package ru.spbstu.sharedplanner.di.deps

import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import ru.spbstu.auth.di.AuthApi
import ru.spbstu.auth.di.AuthFeatureHolder
import ru.spbstu.calendar.di.CalendarApi
import ru.spbstu.calendar.di.CalendarFeatureHolder
import ru.spbstu.sharedplanner.root.di.RootApi
import ru.spbstu.sharedplanner.root.di.RootFeatureHolder
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.sharedplanner.App

@Module
interface ComponentHolderModule {

    @ApplicationScope
    @Binds
    fun provideFeatureContainer(application: App): FeatureContainer

    @ApplicationScope
    @Binds
    @ClassKey(RootApi::class)
    @IntoMap
    fun provideRootFeatureHolder(rootFeatureHolder: RootFeatureHolder): FeatureApiHolder

    @ApplicationScope
    @Binds
    @ClassKey(AuthApi::class)
    @IntoMap
    fun provideAuthFeatureHolder(featureHolder: AuthFeatureHolder): FeatureApiHolder

    @ApplicationScope
    @Binds
    @ClassKey(CalendarApi::class)
    @IntoMap
    fun provideCalendarFeatureHolder(featureHolder: CalendarFeatureHolder): FeatureApiHolder
}