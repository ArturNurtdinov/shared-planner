package ru.spbstu.calendar.settings.groups.edit.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.domain.model.Profile

class CreateGroupViewModel(private val router: CalendarRouter) : ViewModel() {

    var mode: Mode? = null

    private val _state = MutableStateFlow(State(emptyList()))
    val state = _state.asStateFlow()

    fun onBackPressed(): Boolean = router.pop()

    fun openSearch() = router.openSearch()

    fun onSearchAddedResult(added: List<Profile>) {
        val current = _state.value.users
        val newList = mutableListOf<Profile>().apply {
            addAll(current)
            addAll(added)
        }
        _state.value = State(newList.distinct())
    }

    fun deleteUser(profile: Profile) {
        val current = _state.value.users
        val newList = mutableListOf<Profile>().apply {
            addAll(current)
            remove(profile)
        }
        _state.value = State(newList)
    }

    data class State(
        val users: List<Profile>,
    )

    sealed class Mode {
        object CreateGroup : Mode()
        data class EditGroup(val group: Group) : Mode()
    }
}