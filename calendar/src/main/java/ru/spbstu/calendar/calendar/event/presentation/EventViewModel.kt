package ru.spbstu.calendar.calendar.event.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.SharedPlannerResult

class EventViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val errorStringsProvider: ErrorStringsProvider,
) : ViewModel() {
    var event: EventModel? = null
        set(value) {
            if (field != value) {
                field = value
                init(value)
            }
        }

    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    private val downloadsMap: MutableMap<Long, Uri> = mutableMapOf()

    fun onBackClicked(): Boolean = router.pop()
    fun editEvent() = router.openCreateEventFragment(event)

    fun onNewDownloadStarted(id: Long, uri: Uri) {
        downloadsMap[id] = uri
    }

    fun getUriForId(id: Long): Uri? = downloadsMap[id]

    fun deleteEvent() {
        val event = this.event ?: return
        _state.value = _state.value?.copy(deleteInProcess = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (calendarRepository.deleteEvent(event.id)) {
                is SharedPlannerResult.Error -> {
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value?.copy(deleteInProcess = false)
                    }
                    _errorMessage.send(errorStringsProvider.provideActionError())
                }
                is SharedPlannerResult.Success -> {
                    withContext(Dispatchers.Main) {
                        _state.value = _state.value?.copy(deleteInProcess = false)
                        goToMainPageFromEventPage()
                    }
                }
            }
        }
    }

    fun goToMainPageFromEventPage() = router.goToMainPageFromEventPage()

    private val _state = MutableStateFlow<State?>(null)
    val state = _state.asStateFlow()

    private fun init(eventModel: EventModel?) {
        if (eventModel == null) return
        _state.value = State(eventModel, false)
    }

    data class State(val eventModel: EventModel, val deleteInProcess: Boolean)
}