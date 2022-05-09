package ru.spbstu.calendar.settings.groups.edit.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantUi

class SearchViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State(emptyList(), emptyList()))
    val state = _state.asStateFlow()
    var query: String = ""

    val searchFlow = Pager(config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { SearchPagingSource(calendarRepository, query) })
        .flow
        .cachedIn(viewModelScope)

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

    private class SearchPagingSource(
        private val calendarRepository: CalendarRepository,
        private val query: String,
    ) : PagingSource<Int, Profile>() {
        override fun getRefreshKey(state: PagingState<Int, Profile>): Int? {
            // Try to find the page key of the closest page to anchorPosition, from
            // either the prevKey or the nextKey, but you need to handle nullability
            // here:
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey null -> anchorPage is the initial page, so
            //    just return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Profile> {
            val nextPageNumber = params.key ?: 1
            /*val response = backend.searchUsers(query, nextPageNumber) */
            Log.d("WWWW", "nextPage = $nextPageNumber")
            if (nextPageNumber > 40) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null, // Only paging forward.
                    nextKey = null
                )
            }
            return LoadResult.Page(
                data = listOf(
                    Profile(
                        0,
                        "Artur Nurtdinov",
                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
//                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    ),
                    Profile(
                        0,
                        "Artur Nurtdinov",
//                        "https://avatarko.ru/img/kartinka/33/multfilm_lyagushka_32117.jpg",
                        "",
                        "a.nurtdinow@yandex.ru",
                        "+79173863997",
                    )
                ),
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + 1
            )
        }

    }
}