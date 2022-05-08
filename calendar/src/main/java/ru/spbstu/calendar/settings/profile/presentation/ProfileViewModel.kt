package ru.spbstu.calendar.settings.profile.presentation

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
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.errors.ErrorStringsProvider
import ru.spbstu.common.network.SharedPlannerResult

class ProfileViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val errorStringsProvider: ErrorStringsProvider,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<State?>(State.Loading)
    val state = _state.asStateFlow()

    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    init {
        loadState()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = calendarRepository.logout()
            preferencesRepository.clearTokens()
            when (result) {
                is SharedPlannerResult.Success -> {
                    withContext(Dispatchers.Main) {
                        router.goToLogin()
                    }
                }
                is SharedPlannerResult.Error -> {
                    withContext(Dispatchers.Main) {
                        router.goToLogin()
                    }
                }
            }
        }
    }

    private fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = calendarRepository.getUser()
            when (result) {
                is SharedPlannerResult.Success -> {
                    withContext(Dispatchers.Main) {
                        _state.value = State.Data(result.data)
                    }
                }
                is SharedPlannerResult.Error -> {
                    withContext(Dispatchers.Main) {
                        _errorMessage.send(errorStringsProvider.provideDataLoadingError())
                    }
                }
            }
        }
    }

    fun onBackClicked(): Boolean = router.pop()

    sealed class State {
        object Loading : State()
        data class Data(val profile: Profile) : State()
    }
}