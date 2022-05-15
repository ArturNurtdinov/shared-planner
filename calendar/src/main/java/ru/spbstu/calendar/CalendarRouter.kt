package ru.spbstu.calendar

import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.calendar.domain.model.Group

interface CalendarRouter {
    fun pop(): Boolean
    fun openSettings()
    fun openProfile()
    fun openNotificationsSettings()
    fun openGroupsPage()
    fun openSettingsFromCalendar()
    fun goToGroupCreateOrEdit(group: Group?)
    fun openSearch()
    fun openDayPage(date: Long)
    fun openEventFragment(event: EventModel)
    fun openCreateEventFragment(event: EventModel?)
    fun goToLogin()
    fun openCalendarFromEvent()
    fun goToMainPageFromEventPage()
}