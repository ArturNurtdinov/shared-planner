package ru.spbstu.calendar.settings.groups.edit.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantUi

class SearchViewModel(private val router: CalendarRouter) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State(emptyList(), emptyList()))
    val state = _state.asStateFlow()

    init {
        val current = _state.value
        _state.value = current.copy(
            added = listOf(
                ParticipantUi.ParticipantUiItem(
                    Profile(
                        0,
                        "Artur Nurtdinov",
                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
//                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    )
                ),
                ParticipantUi.ParticipantUiItem(
                    Profile(
                        0,
                        "Artur Nurtdinov",
//                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    )
                ),
            ),
            found = listOf(
                Profile(
                    0,
                    "Artur Nurtdinov",
//                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
                    "",
                    "a.nurtdinow@yandex.ru",
                    "+79173863997",
                ),
                Profile(
                    1,
                    "Artur Nurtdinov",
                    "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
//                    "",
                    "a.nurtdinow@yandex.ru",
                    "+79173863997",
                )
            )
        )
    }

    fun onBackPressed(): Boolean = router.pop()

    data class State(
        val added: List<ParticipantUi>,
        val found: List<Profile>,
    )
}