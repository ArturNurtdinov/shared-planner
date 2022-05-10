package ru.spbstu.calendar.calendar.presentation

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
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.SharedPlannerResult

class CalendarViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(State(emptyList()))
    val state = _state.asStateFlow()
    var isManuallySelectedYear = false
    var isManuallySelectedMonth = false
    var shouldScrollOnCreate: Boolean = true

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = calendarRepository.getUser()
            if (currentUser is SharedPlannerResult.Success) {
                val id = currentUser.data.id
                preferencesRepository.setSelfId(id)
            }
        }

        loadData()
    }

    fun onBackClicked(): Boolean = router.pop()
    fun openSettings() = router.openSettings()
    fun navigateToGroupSettings() = router.openSettingsFromCalendar()
    fun openDay(date: Long) = router.openDayPage(date)
    fun openCreateEventPage() = router.openCreateEventFragment(null)

    fun loadData() {
        val currentState = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupsResult = calendarRepository.getGroups()) {
                is SharedPlannerResult.Error -> {

                }
                is SharedPlannerResult.Success -> {
                    val selected = currentState.groups
                    val groups = groupsResult.data
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            groups = groups.map { group ->
                                selected.find { it.id == group.id } ?: GroupSelected(group, true)
                            }
                        )
                    }
                }
            }
        }
    }

    fun onSelectedChange(newGroups: List<GroupSelected>) {
        _state.value = _state.value.copy(
            groups = newGroups
        )
    }

    data class State(
        val groups: List<GroupSelected>
    )
}