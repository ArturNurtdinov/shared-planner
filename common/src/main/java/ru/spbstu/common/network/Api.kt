package ru.spbstu.common.network

import retrofit2.Response
import retrofit2.http.*
import ru.spbstu.common.network.model.*

public interface Api {
    @POST("/auth/signin/google")
    suspend fun authGoogle(@Body codeBody: GoogleAuthCodeBody): Response<TokensResponseBody>

    @POST("/auth/logout")
    suspend fun logout(@Body refreshTokenBody: RefreshTokenBody): Response<Void>

    @GET("/user")
    suspend fun getUser(): Response<UserResponse>

    @GET("/users")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): Response<SearchResponse>

    @POST("/groups")
    suspend fun createGroup(@Body groupBody: GroupBody): Response<Void>

    @GET("/groups")
    suspend fun getGroups(): Response<List<GroupResponse>>

    @GET("/groups/{id}")
    suspend fun getGroupById(@Path("id") id: Long): Response<GroupByIdResponse>

    @PUT("/groups/{id}")
    suspend fun updateGroupById(
        @Path("id") id: Long,
        @Body body: UpdateGroupBody
    ): Response<Void>

    @PUT("/groups/{id}/settings")
    suspend fun updateGroupSettings(
        @Path("id") id: Long,
        @Body updateGroupSettingsBody: UpdateGroupSettingsBody
    ): Response<Void>

    @POST("/events")
    suspend fun createEvent(@Body createEventBody: CreateEventBody): Response<Void>

    @GET("/events")
    suspend fun getEvents(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("group_ids") vararg groupIds: Long
    ): Response<List<EventResponse>>

    @PUT("/events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body updateEventBody: UpdateEventBody
    ): Response<Void>
}