package ru.spbstu.calendar.calendar.event.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Event

class EventViewModel(private val router: CalendarRouter) : ViewModel() {
    var event: Event? = null
    fun onBackClicked(): Boolean = router.pop()
    fun editEvent() = router.openCreateEventFragment(event)
}