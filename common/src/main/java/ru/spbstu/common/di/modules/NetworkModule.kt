package ru.spbstu.common.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.prefs.PreferencesRepository
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.common.events.AuthEvent
import ru.spbstu.common.network.Api
import ru.spbstu.common.network.model.RefreshTokenBody
import ru.spbstu.common.network.model.TokensResponseBody
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
class NetworkModule {

    companion object {
        private val TAG = NetworkModule::class.simpleName!!
        private const val BEARER = "Bearer "
        private const val AUTHORIZATION = "Authorization"
    }

    @Provides
    @ApplicationScope
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @ApplicationScope
    internal fun provideRestInterceptor(
        gson: Gson,
        preferencesRepository: PreferencesRepository,
        @Named("refresh") refreshOkhttpClient: OkHttpClient,
    ): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val accessToken = preferencesRepository.token
            val requestBuilder = original.newBuilder()
            if (accessToken.isNotEmpty() && !original.url.toString().contains("auth")) {
                requestBuilder.addHeader(AUTHORIZATION, BEARER + accessToken)
            }
            val request = requestBuilder.build()
            var response = kotlin.runCatching { chain.proceed(request) }.getOrElse {
                throw IOException("Couldn't procees request. Probably no Internet connection")
            }
            Timber.tag(TAG).i("Processed request(no tokens)=$original, response=$response")
            val code = response.code
            if ((code == 401 || code == 403) && !original.url.toString().contains("auth")) {
                val refreshToken = RefreshTokenBody(preferencesRepository.refresh)
                val authRequest = Request.Builder()
                    .post(gson.toJson(refreshToken).toRequestBody())
                    .url(BuildConfig.REFRESH_ENDPOINT)
                    .build()

                if (refreshToken.refresh.isEmpty()) {
                    return@Interceptor response
                }
//                response.close()
                val refreshTokenResponse = refreshOkhttpClient.newCall(
                    Request.Builder()
                        .post(gson.toJson(refreshToken).toRequestBody())
                        .url(BuildConfig.REFRESH_ENDPOINT)
                        .build()
                ).execute()
//                val refreshTokenResponse = chain.proceed(authRequest)
                if (refreshTokenResponse.code == 200) {
                    val jsonBody = refreshTokenResponse.peekBody(2048).string()
                    val tokens =
                        gson.fromJson(
                            jsonBody,
                            TokensResponseBody::class.java
                        )
                    preferencesRepository.setRefresh(tokens.refresh)
                    preferencesRepository.setToken(tokens.access)
                    val currentRequest = original.newBuilder()
                        .addHeader(AUTHORIZATION, BEARER + tokens.access)
                        .build()
                    Timber.tag(TAG)
                        .d("NetworkModule: Tokens refreshed for $authRequest response=$refreshTokenResponse new_tokens=$tokens")
                    refreshTokenResponse.close()
                    response.close()
                    response = chain.proceed(currentRequest)
                } else if (refreshTokenResponse.code == 401) {
                    refreshTokenResponse.close()
                    Timber.tag(TAG)
                        .d("NetworkModule: Refresh token died response=$refreshTokenResponse")
                    preferencesRepository.clearTokens()
                    EventBus.getDefault().post(AuthEvent())
                }
            }
            response
        }

    @Provides
    @ApplicationScope
    @Named("refresh")
    fun provideOkHttpClientForRefresh(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
        return builder.build()
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(
        restInterceptor: Interceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(restInterceptor)
        return builder.build()
    }

    @Provides
    @ApplicationScope
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @ApplicationScope
    fun provideApi(retrofit: Retrofit): Api = retrofit.create(Api::class.java)
}
