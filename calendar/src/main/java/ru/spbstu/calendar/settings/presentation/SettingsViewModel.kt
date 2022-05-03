package ru.spbstu.calendar.settings.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter

class SettingsViewModel(private val router: CalendarRouter) : ViewModel() {
    fun onBackClicked(): Boolean = router.pop()
    fun openProfile() = router.openProfile()
    fun openNotificationsSettings() = router.openNotificationsSettings()
    fun openGroupsPage() = router.openGroupsPage()
}