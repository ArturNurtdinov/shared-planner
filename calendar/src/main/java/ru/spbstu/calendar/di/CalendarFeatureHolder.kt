package ru.spbstu.calendar.di

import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.common.di.FeatureApiHolder
import ru.spbstu.common.di.FeatureContainer
import ru.spbstu.common.di.scope.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class CalendarFeatureHolder @Inject constructor(
    featureContainer: FeatureContainer,
    private val featureRouter: CalendarRouter
) : FeatureApiHolder(featureContainer) {
    override fun initializeDependencies(): Any {
        val deps = DaggerCalendarComponent_CalendarDependenciesComponent.builder()
            .commonApi(commonApi())
            .build()
        return DaggerCalendarComponent.factory()
            .create(featureRouter, deps)
    }
}