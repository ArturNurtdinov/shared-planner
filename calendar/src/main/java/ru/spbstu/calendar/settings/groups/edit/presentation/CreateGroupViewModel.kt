package ru.spbstu.calendar.settings.groups.edit.presentation

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.network.SharedPlannerResult

class CreateGroupViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    var mode: Mode? = null
        set(value) {
            if (field != value) {
                field = value
                if (value == Mode.CreateGroup) {
                    initCreateMode()
                } else {
                    initEditMode()
                }
            }
        }

    private val _state = MutableStateFlow(State(emptyList(), null, 0))
    val state = _state.asStateFlow()

    private var doneProcessing = false

    private fun initCreateMode() {
        _state.value = _state.value.copy(
            color = Color.BLUE
        )
    }

    private fun initEditMode() {
        val group = (mode as? Mode.EditGroup)?.group ?: return
        _state.value = _state.value.copy(color = group.color)
        viewModelScope.launch(Dispatchers.IO) {
            when (val groupData = calendarRepository.getGroupById(group.id)) {
                is SharedPlannerResult.Error -> {
                    withContext(Dispatchers.Main) {
                        onBackPressed()
                    }
                }
                is SharedPlannerResult.Success -> {
                    val users = mutableListOf<Profile>().apply {
                        addAll(_state.value.users)
                        addAll(groupData.data.users.map {
                            Profile.fromNetworkModel(it)
                        })
                    }
                    _state.value = _state.value.copy(
                        users = users.distinctBy { it.id },
                        creatorId = groupData.data.creatorId,
                    )
                }
            }
        }
    }

    fun onBackPressed(): Boolean = router.pop()

    fun openSearch() = router.openSearch()

    fun confirmGroupEdit(name: String, color: Int) {
        val group = (mode as? Mode.EditGroup)?.group ?: return
        if (doneProcessing) return
        doneProcessing = true
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            val addedIds = _state.value.users.map { it.id }
            calendarRepository.updateGroup(
                group.id,
                name,
                color,
                addedIds,
                group.notificationsEnabled
            )
            doneProcessing = false
            withContext(Dispatchers.Main) {
                onBackPressed()
            }
        }
    }

    fun onSearchAddedResult(added: List<Profile>) {
        val current = _state.value
        val newList = mutableListOf<Profile>().apply {
            addAll(current.users)
            addAll(added)
        }
        _state.value = State(newList.distinct(), current.color, current.creatorId)
    }

    fun selectNewColor(color: Int) {
        _state.value = _state.value.copy(color = color)
    }

    fun deleteUser(profile: Profile) {
        val current = _state.value
        val newList = mutableListOf<Profile>().apply {
            addAll(current.users)
            remove(profile)
        }
        _state.value = State(newList, current.color, current.creatorId)
    }

    fun createGroup(name: String, color: Int) {
        if (doneProcessing) return
        doneProcessing = true
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            val addedIds = _state.value.users.map { it.id }
            calendarRepository.createGroup(name, color, addedIds)
            doneProcessing = false
            withContext(Dispatchers.Main) {
                onBackPressed()
            }
        }
    }

    data class State(
        val users: List<Profile>,
        val color: Int?,
        val creatorId: Long,
    )

    sealed class Mode {
        object CreateGroup : Mode()
        data class EditGroup(val group: Group) : Mode()
    }
}