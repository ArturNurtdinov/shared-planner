package ru.spbstu.calendar.calendar.day.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Event

class DayViewModel(private val router: CalendarRouter) : ViewModel() {
    fun onBackClicked(): Boolean = router.pop()
    fun goToEventPage(event: Event) = router.openEventFragment(event)
}