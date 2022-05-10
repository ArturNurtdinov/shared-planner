package ru.spbstu.calendar.settings.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.presentation.dialog.model.GroupSelected
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.common.network.SharedPlannerResult

class NotificationsViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    init {
        loadData()
    }

    private val _state = MutableStateFlow(State(emptyList()))
    val state = _state.asStateFlow()

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupsResult = calendarRepository.getGroups()) {
                is SharedPlannerResult.Error -> {

                }
                is SharedPlannerResult.Success -> {
                    val groups = groupsResult.data
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(groups = groups)
                    }
                }
            }
        }
    }

    fun onNotificationsStateChanged(newValue: Boolean) {

    }

    fun onNotificationsStateChanged(newValue: Boolean, group: Group) {

    }

    fun onBackClicked(): Boolean = router.pop()

    data class State(val groups: List<Group>)
}