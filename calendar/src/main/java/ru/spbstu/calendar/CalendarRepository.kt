package ru.spbstu.calendar

import android.graphics.Color
import okhttp3.internal.toHexString
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.EmptyResult
import ru.spbstu.common.network.SharedPlannerResult
import ru.spbstu.common.network.UnknownError
import ru.spbstu.common.network.model.*

class CalendarRepository(
    private val api: Api,
    private val preferencesRepository: PreferencesRepository
) {
    suspend fun getUser(): SharedPlannerResult<Profile> {
        val response = api.getUser()
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(Profile.fromNetworkModel(response.body()!!))
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun getGroups(): SharedPlannerResult<List<Group>> {
        val response = api.getGroups()
        return if (response.isSuccessful) {
            val body = response.body()!!
            SharedPlannerResult.Success(body.map {
                Group(
                    it.id,
                    it.name,
                    Color.parseColor(it.color),
                    it.notify,
                    it.userCount
                )
            })
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun createGroup(
        name: String,
        color: Int,
        userIds: List<Long>
    ): SharedPlannerResult<Any> {
        val processedColor = color.toHexString().uppercase()
        val response =
            api.createGroup(
                GroupBody(
                    name,
                    "#${processedColor.substring(2, processedColor.length)}",
                    userIds
                )
            )
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(Any())
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun getGroupById(id: Long): SharedPlannerResult<GroupByIdResponse> {
        val response = api.getGroupById(id)
        return if (response.isSuccessful) {
            val groupData = response.body() ?: return SharedPlannerResult.Error(UnknownError)
            SharedPlannerResult.Success(groupData)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun changeGroupNotificationsState(id: Long, newNotify: Boolean, color: Int) {
        val processedColor = color.toHexString().uppercase()
        api.updateGroupSettings(
            id,
            UpdateGroupSettingsBody(
                "#${processedColor.substring(2, processedColor.length)}",
                newNotify,
            )
        )
    }

    suspend fun updateGroup(
        id: Long,
        name: String,
        color: Int,
        userIds: List<Long>,
        notify: Boolean,
    ) {
        val processedColor = color.toHexString().uppercase()
        api.updateGroupSettings(
            id,
            UpdateGroupSettingsBody(
                "#${processedColor.substring(2, processedColor.length)}",
                notify
            )
        )
        api.updateGroupById(id, UpdateGroupBody(name, userIds))
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
                .map { Profile.fromNetworkModel(it) } to body.page)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    companion object {
        const val SEARCH_PAGE_SIZE = 20
    }
}