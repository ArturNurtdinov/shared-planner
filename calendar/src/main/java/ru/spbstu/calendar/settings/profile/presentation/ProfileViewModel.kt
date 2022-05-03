package ru.spbstu.calendar.settings.profile.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter

class ProfileViewModel(private val router: CalendarRouter) : ViewModel() {
    fun onBackClicked(): Boolean = router.pop()
}