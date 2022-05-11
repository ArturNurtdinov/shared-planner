package ru.spbstu.calendar.settings.groups.edit.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.spbstu.calendar.CalendarRepository
import ru.spbstu.calendar.CalendarRouter
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.calendar.settings.groups.edit.presentation.adapter.ParticipantUi
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.SharedPlannerResult

class SearchViewModel(
    private val router: CalendarRouter,
    private val calendarRepository: CalendarRepository,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<State> = MutableStateFlow(State(emptyList()))
    val state = _state.asStateFlow()
    var query: String = ""

    val selfId: Long
        get() = preferencesRepository.selfId ?: 0L

    val searchFlow = Pager(config = PagingConfig(pageSize = CalendarRepository.SEARCH_PAGE_SIZE),
        pagingSourceFactory = { SearchPagingSource(calendarRepository, query) })
        .flow
        .cachedIn(viewModelScope)

    fun onBackPressed(): Boolean = router.pop()

    fun addUser(profile: Profile) {
        val current = _state.value
        _state.value = current.copy(
            added = mutableListOf<ParticipantUi>().apply {
                addAll(current.added)
                add(ParticipantUi.ParticipantUiItem(profile))
            }
        )
    }

    fun deleteUser(profile: Profile) {
        val current = _state.value
        _state.value = current.copy(
            added = mutableListOf<ParticipantUi>().apply {
                addAll(current.added)
                remove(ParticipantUi.ParticipantUiItem(profile))
            }
        )
    }

    fun getAdded(): List<Profile> {
        val current = _state.value
        return current.added.map { (it as ParticipantUi.ParticipantUiItem).profile }
    }

    data class State(
        val added: List<ParticipantUi>,
    )

    private inner class SearchPagingSource(
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
            if (query.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null,
                )
            }
            return when (val response = calendarRepository.searchUsers(query, nextPageNumber)) {
                is SharedPlannerResult.Success -> {
                    if (response.data.first.isEmpty()) {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = null, // Only paging forward.
                            nextKey = null
                        )
                    } else {
                        val added =
                            _state.value.added.mapNotNull { if (it is ParticipantUi.ParticipantUiItem) it.profile else null }
                        LoadResult.Page(
                            data = response.data.first.filter { found -> added.none { it.id == found.id } && found.id != preferencesRepository.selfId },
                            prevKey = null, // Only paging forward.
                            nextKey = response.data.second + 1
                        )
                    }
                }
                is SharedPlannerResult.Error -> {
                    LoadResult.Page(
                        data = emptyList(),
                        prevKey = null, // Only paging forward.
                        nextKey = null,
                    )
                }
            }
        }

    }
}