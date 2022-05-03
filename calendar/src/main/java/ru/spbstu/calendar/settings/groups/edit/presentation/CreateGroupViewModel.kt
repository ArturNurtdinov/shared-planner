package ru.spbstu.calendar.settings.groups.edit.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Group

class CreateGroupViewModel(private val router: CalendarRouter) : ViewModel() {
    var mode: Mode? = null
    fun onBackPressed(): Boolean = router.pop()
    fun openSearch() = router.openSearch()

    sealed class Mode {
        object CreateGroup : Mode()
        data class EditGroup(val group: Group) : Mode()
    }
}