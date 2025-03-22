package com.example.simplenote.api

import com.example.simplenote.api.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/token/")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    @POST("api/auth/token/refresh/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<TokenResponse>

    @GET("api/auth/userinfo/")
    suspend fun getUserInfo(): Response<UserInfoResponse>

    @GET("api/notes/")
    suspend fun getNotes(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<NotesListResponse>

    @POST("api/notes/")
    suspend fun createNote(@Body request: NoteRequest): Response<NoteResponse>

    @GET("api/notes/{id}/")
    suspend fun getNoteById(@Path("id") id: Int): Response<NoteResponse>

    @PUT("api/notes/{id}/")
    suspend fun updateNote(
        @Path("id") id: Int,
        @Body request: NoteRequest
    ): Response<NoteResponse>

    @DELETE("api/notes/{id}/")
    suspend fun deleteNote(@Path("id") id: Int): Response<Unit>

    @GET("api/notes/filter")
    suspend fun filterNotes(
        @Query("title") title: String? = null,
        @Query("description") description: String? = null,
        @Query("updated__gte") updatedAfter: String? = null,
        @Query("updated__lte") updatedBefore: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): Response<NotesListResponse>
}