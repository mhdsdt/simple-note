package com.example.simplenote.api

import com.example.simplenote.api.models.RefreshTokenRequest
import com.example.simplenote.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val baseUrl: String,
) : Authenticator {

    private val refreshService: ApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite loops by giving up after 3 tries
        if (responseCount(response) >= 3) {
            return null
        }

        // Get the stored refresh token; if none exists, no point in continuing
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        return runBlocking {
            try {
                val refreshResponse = refreshService.refreshToken(
                    RefreshTokenRequest(refreshToken)
                )

                when {
                    refreshResponse.isSuccessful -> {
                        refreshResponse.body()?.let { tokenResponse ->
                            // Save new tokens and retry the request with the new access token
                            tokenManager.saveTokens(tokenResponse.access, tokenResponse.refresh)
                            response.request.newBuilder()
                                .header("Authorization", "Bearer ${tokenResponse.access}")
                                .build()
                        } ?: run {
                            // In case the response body is unexpectedly null,
                            // clear the tokens so that the user is forced to log in again.
                            tokenManager.clearTokens()
                            null
                        }
                    }
                    refreshResponse.code() == 401 -> {
                        // If a 401 is returned, the refresh token is expired or invalid.
                        tokenManager.clearTokens()
                        null
                    }
                    else -> {
                        // Optionally, inspect the error body for extra safety.
                        val errorBody = refreshResponse.errorBody()?.string() ?: ""
                        if (errorBody.contains("token not valid", ignoreCase = true) ||
                            errorBody.contains("Token is invalid or expired", ignoreCase = true)
                        ) {
                            // Clear tokens and force the user to log in again.
                            tokenManager.clearTokens()
                        }
                        null
                    }
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    tokenManager.clearTokens()
                }
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
