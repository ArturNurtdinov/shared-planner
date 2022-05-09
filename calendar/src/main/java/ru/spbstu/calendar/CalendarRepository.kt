package ru.spbstu.calendar

import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.EmptyResult
import ru.spbstu.common.network.SharedPlannerResult
import ru.spbstu.common.network.UnknownError
import ru.spbstu.common.network.model.RefreshTokenBody

class CalendarRepository(
    private val api: Api,
    private val preferencesRepository: PreferencesRepository
) {
    suspend fun getUser(): SharedPlannerResult<Profile> {
        val response = api.getUser()
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(Profile.fromNetworkModule(response.body()!!))
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun logout(): SharedPlannerResult<Any> {
        val response = api.logout(RefreshTokenBody(preferencesRepository.refresh))
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(EmptyResult)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun searchUsers(
        query: String,
        page: Int
    ): SharedPlannerResult<Pair<List<Profile>, Int>> {
        val response = api.searchUsers(query, SEARCH_PAGE_SIZE, page)
        return if (response.isSuccessful) {
            val body = response.body()!!
            SharedPlannerResult.Success(body.users
                .map { Profile.fromNetworkModule(it) } to body.page)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    companion object {
        const val SEARCH_PAGE_SIZE = 20
    }
}