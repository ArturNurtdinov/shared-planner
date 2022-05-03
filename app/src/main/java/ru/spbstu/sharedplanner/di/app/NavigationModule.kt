package ru.spbstu.sharedplanner.di.app

import dagger.Module
import dagger.Provides
import ru.spbstu.auth.AuthRouter
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.sharedplanner.navigation.Navigator
import ru.spbstu.sharedplanner.root.presentation.RootRouter

@Module
class NavigationModule {

    @ApplicationScope
    @Provides
    fun provideNavigator(): Navigator = Navigator()

    @ApplicationScope
    @Provides
    fun provideRootRouter(navigator: Navigator): RootRouter = navigator

    @ApplicationScope
    @Provides
    fun provideAuthRouter(navigator: Navigator): AuthRouter = navigator

    @ApplicationScope
    @Provides
    fun provideCalendarRouter(navigator: Navigator): CalendarRouter = navigator
}