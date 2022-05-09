package ru.spbstu.calendar.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.SharedPlannerResult

class CalendarViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = calendarRepository.getUser()
            if (currentUser is SharedPlannerResult.Success) {
                val id = currentUser.data.id
                preferencesRepository.setSelfId(id)
            }
        }
    }

    var isManuallySelectedYear = false
    var isManuallySelectedMonth = false
    var shouldScrollOnCreate: Boolean = true
    fun onBackClicked(): Boolean = router.pop()
    fun openSettings() = router.openSettings()
    fun navigateToGroupSettings() = router.openSettingsFromCalendar()
    fun openDay(date: Long) = router.openDayPage(date)
    fun openCreateEventPage() = router.openCreateEventFragment(null)
}