package com.example.simplenote.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.simplenote.api.ApiClient
import com.example.simplenote.api.ApiService
import com.example.simplenote.data.local.TokenManager
import com.example.simplenote.data.local.db.AppDatabase
import com.example.simplenote.data.local.db.NoteDao
import com.example.simplenote.data.repository.AuthRepository
import com.example.simplenote.data.repository.NoteRepository
import com.example.simplenote.sync.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideApiClient(tokenManager: TokenManager): ApiClient {
        return ApiClient(tokenManager)
    }

    @Provides
    @Singleton
    fun provideApiService(apiClient: ApiClient): ApiService {
        return apiClient.apiService
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService, tokenManager: TokenManager): AuthRepository {
        return AuthRepository(apiService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "simplenote_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return NetworkMonitor(context)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        apiService: ApiService,
        noteDao: NoteDao,
        workManager: WorkManager
    ): NoteRepository {
        return NoteRepository(apiService, noteDao, workManager)
    }
}