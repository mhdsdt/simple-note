package com.example.simplenote.api

import com.example.simplenote.api.utils.LocalDateTimeDeserializer
import com.example.simplenote.data.local.TokenManager
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(private val tokenManager: TokenManager) {


    private val baseUrl = "https://simple-note.liara.run/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = tokenManager.getAccessToken()

        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val tokenAuthenticator = TokenAuthenticator(tokenManager, baseUrl)

    private val okHttpClient = OkHttpClient.Builder()
        .authenticator(tokenAuthenticator)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Pattern should match whatever your API returns; e.g. "2025-07-07T15:30:00Z"
    private val timestampPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"

    private val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(timestampPattern)
        )
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}

