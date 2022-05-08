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
}