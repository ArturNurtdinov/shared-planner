package ru.spbstu.common.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.spbstu.common.BuildConfig
import ru.spbstu.common.di.scope.ApplicationScope
import ru.spbstu.common.network.Api
import java.util.concurrent.TimeUnit

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

    /*@Provides
    @ApplicationScope
    internal fun provideRestInterceptor(
        gson: Gson,
        tokenRepository: TokensRepository
    ): Interceptor =
        Interceptor { chain ->
            val original = chain.request()
            val accessToken = tokenRepository.getToken()
            val requestBuilder = original.newBuilder()
            if (!accessToken.isNullOrEmpty() && !original.url.toString().contains("auth")) {
                requestBuilder.addHeader(AUTHORIZATION, BEARER + accessToken)
            }
            val request = requestBuilder.build()
            var response = kotlin.runCatching { chain.proceed(request) }.getOrElse {
                throw IOException("Couldn't procees request. Probably no Internet connection")
            }
            Timber.tag(TAG).i("Processed request(no tokens)=$original, response=$response")
            if ((response.code == 401 || response.code == 403) && !original.url.toString().contains("auth")) {
                val refreshToken: RefreshToken? = tokenRepository.getRefresh()
                val authRequest = request.newBuilder()
                    .post(gson.toJson(refreshToken).toRequestBody())
                    .url(BuildConfig.REFRESH_ENDPOINT)
                    .build()

                response.close()
                val refreshTokenResponse = chain.proceed(authRequest)
                if (refreshTokenResponse.code == 200) {
                    val tokens =
                        gson.fromJson(
                            refreshTokenResponse.body!!.string(),
                            TokensResponse::class.java
                        )
                    tokenRepository.saveRefresh(tokens.refreshToken)
                    tokenRepository.saveToken(tokens.accessToken)
                    val currentRequest = original.newBuilder()
                        .addHeader(AUTHORIZATION, BEARER + tokens.accessToken)
                        .build()
                    Timber.tag(TAG)
                        .d("NetworkModule: Tokens refreshed for $authRequest response=$refreshTokenResponse new_tokens=$tokens")
                    refreshTokenResponse.close()
                    response = chain.proceed(currentRequest)
                } else if (refreshTokenResponse.code == 401) {
                    Timber.tag(TAG)
                        .d("NetworkModule: Refresh token died response=$refreshTokenResponse")
                    EventBus.getDefault().post(AuthEvent())
                }
            }
            response
        }*/

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(
//        restInterceptor: Interceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(restInterceptor)
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
