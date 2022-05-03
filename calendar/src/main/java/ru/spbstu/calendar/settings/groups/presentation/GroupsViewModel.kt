package ru.spbstu.calendar.settings.groups.presentation

import androidx.lifecycle.ViewModel
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Group

class GroupsViewModel(private val router: CalendarRouter) : ViewModel() {
    fun onBackClicked(): Boolean = router.pop()
    fun openCreateGroupPage() = router.goToGroupCreateOrEdit(null)
    fun openEditGroupPage(group: Group) = router.goToGroupCreateOrEdit(group)
}