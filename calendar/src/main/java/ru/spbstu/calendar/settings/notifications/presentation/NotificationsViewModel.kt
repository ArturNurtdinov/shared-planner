package ru.spbstu.calendar.settings.notifications.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter

class NotificationsViewModel(private val router: CalendarRouter) : ViewModel() {
    fun onBackClicked(): Boolean = router.pop()
}