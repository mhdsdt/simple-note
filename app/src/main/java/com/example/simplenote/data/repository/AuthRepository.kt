package com.example.simplenote.data.repository

import com.example.simplenote.api.ApiService
import com.example.simplenote.api.models.ChangePasswordRequest
import com.example.simplenote.api.models.LoginRequest
import com.example.simplenote.api.models.RefreshTokenRequest
import com.example.simplenote.api.models.RegisterRequest
import com.example.simplenote.api.models.UserInfoResponse
import com.example.simplenote.data.local.TokenManager
import com.example.simplenote.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    fun register(
        username: String,
        password: String,
        email: String,
        firstName: String,
        lastName: String
    ): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequest(username, password, email, firstName, lastName)
            val response = apiService.register(request)

            if (response.isSuccessful) {
                emit(Resource.Success(true))
            } else {
                val raw = response.errorBody()?.string()
                val errorMessage = parseDrfErrorBody("Registration failed:", raw)
                emit(Resource.Error(message = errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(
                Resource.Error("Couldn't reach server. Check your internet connection.")
            )
        }
    }

    fun login(username: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val request = LoginRequest(username, password)
            val response = apiService.login(request)

            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    tokenManager.saveTokens(tokenResponse.access, tokenResponse.refresh)
                    emit(Resource.Success(true))
                } ?: emit(Resource.Error("Login response is null"))
            } else {
                val raw = response.errorBody()?.string()
                val errorMessage = parseDrfErrorBody("Login failed:", raw)
                emit(Resource.Error(message = errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(
                Resource.Error("Couldn't reach server. Check your internet connection.")
            )
        }
    }

    fun refreshToken(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                emit(Resource.Error("No refresh token found"))
                return@flow
            }

            val request = RefreshTokenRequest(refreshToken)
            val response = apiService.refreshToken(request)

            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    tokenManager.saveTokens(tokenResponse.access, tokenResponse.refresh)
                    emit(Resource.Success(true))
                }
            } else {
                val raw = response.errorBody()?.string()
                val errorMessage = parseDrfErrorBody("Token refresh failed:", raw)
                emit(Resource.Error(message = errorMessage))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(
                Resource.Error("Couldn't reach server. Check your internet connection.")
            )
        }
    }

    fun getUserInfo(): Flow<Resource<UserInfoResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUserInfo()
            if (response.isSuccessful) {
                response.body()?.let { userInfo ->
                    emit(Resource.Success(userInfo))
                } ?: emit(Resource.Error("User info response is null"))
            } else {
                if (response.code() == 401) {
                    tokenManager.clearTokens()
                    emit(Resource.Error("Session expired. Please login again."))
                } else {
                    val raw = response.errorBody()?.string()
                    val errorMessage = parseDrfErrorBody("Failed to get user info:", raw)
                    emit(Resource.Error(message = errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
        }
    }

    suspend fun logout() {
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }

    fun changePassword(oldPassword: String, newPassword: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            try {
                val request = ChangePasswordRequest(oldPassword, newPassword)
                val response = apiService.changePassword(request)
                if (response.isSuccessful) {
                    emit(Resource.Success(true))
                } else {
                    val raw = response.errorBody()?.string()
                    val errorMessage = parseDrfErrorBody("Change password failed:", raw)
                    emit(Resource.Error(message = errorMessage))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("An unexpected error occurred: ${e.localizedMessage}"))
            } catch (e: IOException) {
                emit(
                    Resource.Error("Couldn't reach server. Check your internet connection.")
                )
            }
        }
}