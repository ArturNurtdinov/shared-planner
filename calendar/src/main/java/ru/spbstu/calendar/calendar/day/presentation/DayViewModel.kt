package ru.spbstu.calendar.calendar.day.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.common.network.SharedPlannerResult
import java.time.*

class DayViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    var date: LocalDateTime? = null
        set(value) {
            if (field != value) {
                field = value
                init()
            }
        }

    private val _state = MutableStateFlow(
        State(emptyList(), false, true)
    )
    val state = _state.asStateFlow()

    private fun init() {
        val currentState = _state.value
        val date = date ?: return
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupsResult = calendarRepository.getGroups()) {
                is SharedPlannerResult.Error -> {

                }
                is SharedPlannerResult.Success -> {
                    val groups = groupsResult.data
                    val eventsResult = calendarRepository.getEvents(
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(
                                date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            ),
                            ZoneId.systemDefault()
                        ),
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(
                                date.plusDays(1).atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli()
                            ),
                            ZoneId.systemDefault()
                        ),
                        groups,
                    )

                    when (eventsResult) {
                        is SharedPlannerResult.Error -> {
                            _state.value = currentState.copy(
                                events = emptyList(),
                                error = true,
                                loading = false,
                            )
                        }
                        is SharedPlannerResult.Success -> {
                            withContext(Dispatchers.Main) {
                                _state.value = currentState.copy(
                                    events = eventsResult.data,
                                    error = false,
                                    loading = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onBackClicked(): Boolean = router.pop()
    fun goToEventPage(event: EventModel) = router.openEventFragment(event)

    data class State(
        val events: List<EventModel>,
        val error: Boolean,
        val loading: Boolean,
    )
}