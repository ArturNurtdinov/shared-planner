package ru.spbstu.common.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.spbstu.common.network.model.GoogleAuthCodeBody
import ru.spbstu.common.network.model.RefreshTokenBody
import ru.spbstu.common.network.model.TokensResponseBody
import ru.spbstu.common.network.model.UserResponse

public interface Api {
    @POST("/auth/signin/google")
    suspend fun authGoogle(@Body codeBody: GoogleAuthCodeBody): Response<TokensResponseBody>

    @POST("/auth/logout")
    suspend fun logout(@Body refreshTokenBody: RefreshTokenBody): Response<Void>

    @GET("/user")
    suspend fun getUser(): Response<UserResponse>
}