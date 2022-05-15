package ru.spbstu.calendar

import android.graphics.Color
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.internal.toHexString
import ru.spbstu.calendar.domain.model.EventModel
import ru.spbstu.calendar.domain.model.Group
import ru.spbstu.calendar.domain.model.Profile
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.domain.EventTypes
import ru.spbstu.common.domain.NotificationsTypes
import ru.spbstu.common.domain.RepeatTypes
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.EmptyResult
import ru.spbstu.common.network.SharedPlannerResult
import ru.spbstu.common.network.UnknownError
import ru.spbstu.common.network.model.*
import java.io.File
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

    suspend fun pushFcmToken(token: String) {
        api.pushToken(PushTokenBody(token))
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

    suspend fun updateEvent(
        id: String,
        groupId: Long,
        eventTypes: EventTypes,
        title: String,
        description: String,
        allDay: Boolean,
        from: ZonedDateTime,
        to: ZonedDateTime,
        notificationsTypes: List<NotificationsTypes>,
    ): SharedPlannerResult<Any> {
        val fromFormatted = from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val toFormatted = to.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val response = api.updateEvent(
            id,
            UpdateEventBody(
                false,
                groupId,
                eventTypes.ordinal,
                title,
                description,
                allDay,
                fromFormatted,
                toFormatted,
                notificationsTypes.map { it.ordinal },
            )
        )
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(Any())
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun createEvent(
        groupId: Long,
        eventTypes: EventTypes,
        title: String,
        description: String,
        allDay: Boolean,
        from: ZonedDateTime,
        to: ZonedDateTime,
        repeatTypes: RepeatTypes,
        notificationsTypes: List<NotificationsTypes>,
        files: List<Uri>
    ): SharedPlannerResult<Any> {
        val fromFormatted = from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val toFormatted = to.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val filesResponse = files.mapNotNull { if (it.path == null) null else uploadFile(File(it.path!!)) }
        val successFiles = filesResponse.filterIsInstance<SharedPlannerResult.Success<AttachResponse>>()

        val response = api.createEvent(
            CreateEventBody(
                groupId,
                eventTypes.ordinal,
                title,
                description,
                allDay,
                fromFormatted,
                toFormatted,
                repeatTypes.ordinal,
                notificationsTypes.map { it.ordinal },
                successFiles.map { it.data }
            )
        )
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(Any())
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    private suspend fun uploadFile(file: File): SharedPlannerResult<AttachResponse> {
        val body = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )

        val response = api.uploadFile(body)
        response.isSuccessful
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(response.body()!!)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun getEvents(
        from: ZonedDateTime,
        to: ZonedDateTime,
        groups: List<Group>
    ): SharedPlannerResult<List<EventModel>> {
        val groupIds = groups.map { it.id }
        val arrayGroups = LongArray(groupIds.size)
        groupIds.forEachIndexed { index, l ->
            arrayGroups[index] = l
        }
        val response = api.getEvents(
            from.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), to.format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME
            ), *arrayGroups
        )

        return if (response.isSuccessful) {
            SharedPlannerResult.Success(response.body()!!.map { responseModel ->
                EventModel.fromNetworkModel(
                    responseModel,
                    groups.first { it.id == responseModel.groupId })
            })
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    suspend fun deleteEvent(id: String): SharedPlannerResult<Any> {
        val response = api.deleteEvent(id, OnlyDeleteInstanceBody((false)))
        return if (response.isSuccessful) {
            SharedPlannerResult.Success(EmptyResult)
        } else {
            SharedPlannerResult.Error(UnknownError)
        }
    }

    companion object {
        const val SEARCH_PAGE_SIZE = 20
    }
}