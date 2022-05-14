package ru.spbstu.calendar.calendar.event.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.EventModel

class EventViewModel(private val router: CalendarRouter) : ViewModel() {
    var event: EventModel? = null
        set(value) {
            if (field != value) {
                field = value
                init(value)
            }
        }

    fun onBackClicked(): Boolean = router.pop()
    fun editEvent() = router.openCreateEventFragment(event)

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private fun init(eventModel: EventModel?) {
        if (eventModel == null) return
        _state.value = State(eventModel)
    }

    data class State(val eventModel: EventModel)
}