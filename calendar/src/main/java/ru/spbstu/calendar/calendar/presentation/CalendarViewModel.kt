package ru.spbstu.calendar.calendar.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter

class CalendarViewModel(private val router: CalendarRouter) : ViewModel() {
    var isManuallySelectedYear = false
    var isManuallySelectedMonth = false
    var shouldScrollOnCreate: Boolean = true
    fun onBackClicked(): Boolean = router.pop()
    fun openSettings() = router.openSettings()
    fun navigateToGroupSettings() = router.openSettingsFromCalendar()
    fun openDay(date: Long) = router.openDayPage(date)
    fun openCreateEventPage() = router.openCreateEventFragment(null)
}