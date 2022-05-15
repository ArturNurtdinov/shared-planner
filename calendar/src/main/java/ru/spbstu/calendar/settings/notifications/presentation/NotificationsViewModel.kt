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
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.SharedPlannerResult

class NotificationsViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    init {
        loadData()
    }

    private val _state = MutableStateFlow(State(emptyList(), preferencesRepository.notifsEnabled))
    val state = _state.asStateFlow()

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupsResult = calendarRepository.getGroups()) {
                is SharedPlannerResult.Error -> {

                }
                is SharedPlannerResult.Success -> {
                    val groups = groupsResult.data
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(
                            groups = groups,
                            notificationsEnabled = preferencesRepository.notifsEnabled
                        )
                    }
                }
            }
        }
    }

    fun onNotificationsStateChanged(newValue: Boolean) {
        preferencesRepository.setNotifsEnabled(newValue)
    }

    fun onNotificationsStateChanged(newValue: Boolean, group: Group) {
        preferencesRepository.setNotifsEnabledForGroupId(group.id, newValue)
        viewModelScope.launch(Dispatchers.IO) {
            calendarRepository.changeGroupNotificationsState(group.id, newValue, group.color)
            loadData()
        }
    }

    fun onBackClicked(): Boolean = router.pop()

    data class State(val groups: List<Group>, val notificationsEnabled: Boolean)
}