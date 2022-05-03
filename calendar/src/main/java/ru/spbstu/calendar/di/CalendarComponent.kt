package ru.spbstu.calendar.di

import dagger.BindsInstance
import dagger.Component
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.day.di.DaySubComponent
import ru.spbstu.calendar.calendar.di.CalendarSubComponent
import ru.spbstu.calendar.calendar.event.di.EventSubComponent
import ru.spbstu.calendar.calendar.event.edit.di.EditEventSubComponent
import ru.spbstu.calendar.settings.di.SettingsSubComponent
import ru.spbstu.calendar.settings.groups.di.GroupsSubComponent
import ru.spbstu.calendar.settings.groups.edit.di.CreateGroupSubComponent
import ru.spbstu.calendar.settings.groups.edit.search.di.SearchSubComponent
import ru.spbstu.calendar.settings.notifications.di.NotificationsSubComponent
import ru.spbstu.calendar.settings.profile.di.ProfileSubComponent
import ru.spbstu.common.di.CommonApi
import ru.spbstu.common.di.scope.FeatureScope

@Component(
    dependencies = [
        CalendarDependencies::class,
    ],
    modules = [
        CalendarModule::class,
        CalendarDataModule::class
    ]
)
@FeatureScope
interface CalendarComponent {

    fun calendarComponentFactory(): CalendarSubComponent.Factory
    fun settingsComponentFactory(): SettingsSubComponent.Factory
    fun profileComponentFactory(): ProfileSubComponent.Factory
    fun notificationsComponentFactory(): NotificationsSubComponent.Factory
    fun groupsComponentFactory(): GroupsSubComponent.Factory
    fun createGroupComponentFactory(): CreateGroupSubComponent.Factory
    fun searchComponentFactory(): SearchSubComponent.Factory
    fun dayComponentFactory(): DaySubComponent.Factory
    fun eventComponentFactory(): EventSubComponent.Factory
    fun createEventComponentFactory(): EditEventSubComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance featureRouter: CalendarRouter,
            deps: CalendarDependencies
        ): CalendarComponent
    }

    @Component(
        dependencies = [
            CommonApi::class,
        ]
    )
    interface CalendarDependenciesComponent : CalendarDependencies
}