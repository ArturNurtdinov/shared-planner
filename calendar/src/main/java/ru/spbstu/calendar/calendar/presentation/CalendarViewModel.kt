package ru.spbstu.calendar.calendar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.calendar.presentation.dialog.model.GroupSelected
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.network.SharedPlannerResult
import timber.log.Timber
import java.time.*

class CalendarViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val today = LocalDate.now()
    private val _state = MutableStateFlow(
        State(
            emptyList(),
            today.minusMonths(3).withDayOfMonth(1)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            today.plusMonths(3).withDayOfMonth(1)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            emptyList(),
            emptyMap()
        )
    )
    val state = _state.asStateFlow()
    var isManuallySelectedYear = false
    var isManuallySelectedMonth = false
    var shouldScrollOnCreate: Boolean = true

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.tag(TAG).e("Fetching FCM registration token failed: ${task.exception}")
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Timber.tag(TAG).d("newToken on start: $token")
            viewModelScope.launch(Dispatchers.IO) {
                calendarRepository.pushFcmToken(token)
            }
        }
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
                    groups.forEach {
                        preferencesRepository.setNotifsEnabledForGroupId(
                            it.id,
                            it.notificationsEnabled
                        )
                    }
                    val eventsResult = calendarRepository.getEvents(
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(currentState.startTimestamp),
                            ZoneId.systemDefault()
                        ),
                        ZonedDateTime.ofInstant(
                            Instant.ofEpochMilli(currentState.endTimestamp),
                            ZoneId.systemDefault()
                        ),
                        groups,
                    )

                    when (eventsResult) {
                        is SharedPlannerResult.Error -> {

                        }
                        is SharedPlannerResult.Success -> {
                            withContext(Dispatchers.Main) {
                                val eventsByDay =
                                    mutableMapOf<LocalDate, MutableList<EventModel>>()
                                eventsResult.data.forEach {
                                    val listFrom =
                                        eventsByDay[it.from.toLocalDate()] ?: mutableListOf()
                                    listFrom.add(it)
                                    eventsByDay[it.from.toLocalDate()] =
                                        listFrom.distinctBy { it.id }.toMutableList()

                                    val listTo =
                                        eventsByDay[it.to.toLocalDate()] ?: mutableListOf()
                                    listTo.add(it)
                                    eventsByDay[it.to.toLocalDate()] =
                                        listTo.distinctBy { it.id }.toMutableList()

                                    if (it.eventType == EventTypes.EVENT) {
                                        var day = it.from.plusDays(1)
                                        while (day.isBefore(it.to)) {
                                            val list = eventsByDay[day.toLocalDate()]
                                                ?: mutableListOf()
                                            list.add(it)
                                            eventsByDay[day.toLocalDate()] =
                                                list.distinctBy { it.id }.toMutableList()
                                            day = day.plusDays(1)
                                        }
                                    }
                                }
                                _state.value = currentState.copy(
                                    groups = groups.map { group ->
                                        selected.find { it.id == group.id } ?: GroupSelected(
                                            group,
                                            true
                                        )
                                    },
                                    events = eventsResult.data,
                                    eventsByDay = eventsByDay,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onScolledToNewDate(localDate: LocalDate) {
        val start = localDate.minusMonths(3).withDayOfMonth(1)
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val end = localDate.plusMonths(3).withDayOfMonth(1)
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        _state.value = _state.value.copy(
            startTimestamp = start,
            endTimestamp = end
        )
        loadData()
    }

    fun onSelectedChange(newGroups: List<GroupSelected>) {
        _state.value = _state.value.copy(
            groups = newGroups
        )
    }

    data class State(
        val groups: List<GroupSelected>,
        val startTimestamp: Long,
        val endTimestamp: Long,
        val events: List<EventModel>,
        val eventsByDay: Map<LocalDate, List<EventModel>>
    )

    companion object {
        val TAG = CalendarViewModel::class.simpleName!!
    }
}