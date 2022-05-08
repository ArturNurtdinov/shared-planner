package ru.spbstu.auth

import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.EmptyResult
import ru.spbstu.common.network.SharedPlannerResult
import ru.spbstu.common.network.UnknownError
import ru.spbstu.common.network.model.GoogleAuthCodeBody
import timber.log.Timber

class AuthRepository(
    private val api: Api,
    private val preferencesRepository: PreferencesRepository
) {

    suspend fun authGoogle(code: String): SharedPlannerResult<Any> {
        val response = api.authGoogle(GoogleAuthCodeBody(code))
        return if (response.isSuccessful) {
            response.body()?.let {
                preferencesRepository.setToken(it.access)
                preferencesRepository.setRefresh(it.refresh)
            }
            SharedPlannerResult.Success(EmptyResult)
        } else {
            Timber.tag(TAG).e("authGoogle failed: $response")
            SharedPlannerResult.Error(UnknownError)
        }
    }

    private companion object {
        val TAG = AuthRepository::class.simpleName!!
    }

}