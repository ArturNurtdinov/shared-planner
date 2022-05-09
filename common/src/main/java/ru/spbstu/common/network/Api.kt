package ru.spbstu.common.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
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

}