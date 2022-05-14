package ru.spbstu.calendar.calendar.event.edit.presentation

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Event
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.SharedPlannerResult
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class CreateEventViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val contentResolver: ContentResolver,
    private val errorStringsProvider: ErrorStringsProvider,
) : ViewModel() {
    var mode: Mode? = null
        set(value) {
            if (field != value) {
                field = value
                if (value == Mode.CreateEvent) {
                    initCreateMode()
                } else if (value is Mode.EditEvent) {
                    initEditMode(value.event)
                }
            }
        }
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

    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    fun onBackClicked(): Boolean = router.pop()

    private val _state = MutableStateFlow(
        State(
            LocalDate.of(today.year, today.month, today.dayOfMonth),
            LocalDate.of(today.year, today.month, today.dayOfMonth),
            selectedTimeFirst = today,
            selectedTimeSecond = today,
            isAllDay = true,
            isReminder = true,
            repeatItem = RepeatTypes.NONE,
            listOf(NotificationsTypes.MIN_15),
            emptyList(),
            null,
            emptyList(),
            emptyList(),
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val result = calendarRepository.getGroups()
            if (result is SharedPlannerResult.Success) {
                _state.value = _state.value.copy(
                    groups = result.data
                )
            }
        }
    }

    private fun initCreateMode() {

    }

    private fun initEditMode(eventModel: EventModel?) {
        if (eventModel == null) return
        _state.value = _state.value.copy(
            selectedDateFirst = eventModel.from.toLocalDate(),
            selectedDateSecond = eventModel.to.toLocalDate(),
            selectedTimeFirst = eventModel.from.toLocalDateTime(),
            selectedTimeSecond = eventModel.to.toLocalDateTime(),
            isAllDay = eventModel.allDay,
            isReminder = eventModel.eventType == EventTypes.NOTIFICATION,
            repeatItem = eventModel.repeatType,
            notificationsTypes = eventModel.notifications,
            selectedGroup = eventModel.group,
            pickedFiles = eventModel.attaches,
            files = eventModel.attaches.mapNotNull { it.lastPathSegment },
        )
    }

    fun onNotifItemSelected(which: Int) {
        val current = _state.value
        _state.value = current.copy(
            notificationsTypes = mutableListOf<NotificationsTypes>().apply {
                addAll(current.notificationsTypes)
                add(NotificationsTypes.fromInt(which))
            }.distinct()
        )
    }

    fun onGroupSelected(which: Int) {
        val current = _state.value
        _state.value = current.copy(
            selectedGroup = current.groups[which]
        )
    }

    fun onNewFilePicked(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                cursor.moveToFirst()
                val size = cursor.getLong(sizeIndex)
                if (size > 5 * 1024 * 1024) {
                    _errorMessage.send(errorStringsProvider.provideFileSizeError())
                    return@use
                }
            }
            contentResolver.openInputStream(uri)!!.use { input ->
                val file = File(
                    errorStringsProvider.appContext.filesDir.absolutePath,
                    getFileName(uri) ?: System.currentTimeMillis().toString()
                )
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(20 * 2048) // buffer size
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                    val current = _state.value
                    _state.value = current.copy(
                        pickedFiles = mutableListOf<Uri>().apply {
                            addAll(current.pickedFiles)
                            Uri.fromFile(file)
                        },
                        files = mutableListOf<String>().apply {
                            addAll(current.files)
                            add(file.name)
                        }
                    )
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (columnIndex > 0) {
                        result = cursor.getString(columnIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    fun removeNotif(notificationUi: NotificationUi.NotificationUiItem) {
        val value = notificationUi.notification.value
        val current = _state.value
        _state.value = current.copy(
            notificationsTypes = mutableListOf<NotificationsTypes>().apply {
                addAll(current.notificationsTypes)
                remove(NotificationsTypes.fromInt(value))
            }
        )
    }

    fun createEvent(title: String, description: String) {
        if (mode != Mode.CreateEvent) return
        viewModelScope.launch(Dispatchers.IO) {
            val state = _state.value
            val from = state.selectedDateFirst.atTime(
                state.selectedTimeFirst.hour,
                state.selectedTimeFirst.minute
            )

            val to = if (!state.isReminder) state.selectedDateSecond.atTime(
                state.selectedTimeSecond.hour,
                state.selectedTimeSecond.minute
            ) else from

            if (from.isAfter(to) || state.selectedGroup == null) {
                if (state.selectedGroup == null) {
                    _errorMessage.send(errorStringsProvider.provideNoGroupSelectedError())
                } else {
                    _errorMessage.send(errorStringsProvider.provideWrongTimeError())
                }
                return@launch
            }

            val result = calendarRepository.createEvent(
                state.selectedGroup.id,
                if (state.isReminder) EventTypes.NOTIFICATION else EventTypes.EVENT,
                title,
                description,
                state.isAllDay,
                from.atZone(ZoneId.systemDefault()),
                to.atZone(ZoneId.systemDefault()),
                state.repeatItem,
                state.notificationsTypes,
                state.pickedFiles,
            )

            if (result is SharedPlannerResult.Success) {
                withContext(Dispatchers.Main) {
                    onBackClicked()
                }
            } else if (result is SharedPlannerResult.Error) {

            }
        }
    }

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

    private fun getRepeatItemByIndex(index: Int): RepeatTypes {
        return RepeatTypes.fromInt(index)
    }

    sealed class Mode {
        object CreateEvent : Mode()
        data class EditEvent(val event: EventModel?) : Mode()
    }

    data class State(
        val selectedDateFirst: LocalDate,
        val selectedDateSecond: LocalDate,
        val selectedTimeFirst: LocalDateTime,
        val selectedTimeSecond: LocalDateTime,
        val isAllDay: Boolean,
        val isReminder: Boolean,
        val repeatItem: RepeatTypes,
        val notificationsTypes: List<NotificationsTypes>,
        val groups: List<Group>,
        val selectedGroup: Group?,
        val pickedFiles: List<Uri>,
        val files: List<String>,
    )
}