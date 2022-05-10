package ru.spbstu.calendar.settings.groups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.settings.groups.presentation.model.GroupUi
import ru.spbstu.common.network.SharedPlannerResult

class GroupsViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State(emptyList()))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupsResult = calendarRepository.getGroups()) {
                is SharedPlannerResult.Error -> {
                    _state.value = _state.value.copy(
                        groups = listOf(GroupUi.CreateGroupItem)
                    )
                }
                is SharedPlannerResult.Success -> {
                    val groups = groupsResult.data.map { GroupUi.GroupUiItem(it) }
                    val newGroups = mutableListOf<GroupUi>().apply {
                        addAll(groups)
                        add(GroupUi.CreateGroupItem)
                    }
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value.copy(groups = newGroups)
                    }
                }
            }
        }
    }

    fun onBackClicked(): Boolean = router.pop()
    fun openCreateGroupPage() = router.goToGroupCreateOrEdit(null)
    fun openEditGroupPage(group: Group) = router.goToGroupCreateOrEdit(group)

    data class State(
        val groups: List<GroupUi>
    )
}