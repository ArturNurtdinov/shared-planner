package ru.spbstu.calendar.di

import dagger.Module
import dagger.Provides
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.scope.FeatureScope
import ru.spbstu.common.network.Api

@Module
class CalendarDataModule {
    @Provides
    @FeatureScope
    fun provideCalendarRepository(
        api: Api,
        preferencesRepository: PreferencesRepository
    ): CalendarRepository =
        CalendarRepository(api, preferencesRepository)
}