package ru.spbstu.calendar.calendar.event.edit.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class CreateEventViewModel(private val router: CalendarRouter) : ViewModel() {
    var mode: Mode? = null
    var selectedIndex = 0
        set(value) {
            if (field != value) {
                _state.value = _state.value.copy(
                    isReminder = value == 0
                )
                field = value
            }
        }

    val today = LocalDateTime.now()

    fun onBackClicked(): Boolean = router.pop()

    private val _state = MutableStateFlow(
        State(
            LocalDate.of(today.year, today.month, today.dayOfMonth),
            LocalDate.of(today.year, today.month, today.dayOfMonth),
            selectedTimeFirst = today,
            selectedTimeSecond = today,
            isAllDay = true,
            isReminder = true,
            repeatItem = RepeatItem.None,
        )
    )
    val state = _state.asStateFlow()

    fun onFirstDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        _state.value = _state.value.copy(
            selectedDateFirst = LocalDate.of(year, month, dayOfMonth)
        )
    }

    fun onFirstTimeSelected(hour: Int, minute: Int) {
        val currentSelected = _state.value.selectedTimeFirst
        _state.value = _state.value.copy(
            selectedTimeFirst = currentSelected.withHour(hour).withMinute(minute)
        )
    }

    fun onSecondTimeSelected(hour: Int, minute: Int) {
        val currentSelected = _state.value.selectedTimeSecond
        _state.value = _state.value.copy(
            selectedTimeSecond = currentSelected.withHour(hour).withMinute(minute)
        )
    }

    fun onNewAllDayValue(newValue: Boolean) {
        _state.value = _state.value.copy(
            isAllDay = newValue
        )
    }

    fun onSecondDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        _state.value = _state.value.copy(
            selectedDateSecond = LocalDate.of(year, month, dayOfMonth)
        )
    }

    fun onRepeatItemSelected(which: Int) {
        _state.value = _state.value.copy(
            repeatItem = getRepeatItemByIndex(which)
        )
    }

    private fun getRepeatItemByIndex(index: Int): RepeatItem {
        return when (index) {
            0 -> RepeatItem.None
            1 -> RepeatItem.EveryDay
            2 -> RepeatItem.Every3Days
            3 -> RepeatItem.EveryWeek
            4 -> RepeatItem.EveryMonth
            5 -> RepeatItem.EveryYear
            else -> RepeatItem.None
        }
    }

    sealed class Mode {
        object CreateEvent : Mode()
        data class EditEvent(val event: Event?) : Mode()
    }

    data class State(
        val selectedDateFirst: LocalDate,
        val selectedDateSecond: LocalDate,
        val selectedTimeFirst: LocalDateTime,
        val selectedTimeSecond: LocalDateTime,
        val isAllDay: Boolean,
        val isReminder: Boolean,
        val repeatItem: RepeatItem,
    )

    sealed class RepeatItem {
        object None : RepeatItem()
        object EveryDay : RepeatItem()
        object Every3Days : RepeatItem()
        object EveryWeek : RepeatItem()
        object EveryMonth : RepeatItem()
        object EveryYear : RepeatItem()
    }
}