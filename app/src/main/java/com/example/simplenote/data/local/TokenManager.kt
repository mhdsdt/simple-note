package com.example.simplenote.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    fun getAccessToken(): String? {
        var token: String? = null
        try {
            runBlocking {
                context.dataStore.data.map { preferences ->
                    preferences[accessTokenKey]
                }.let {
                    token = it.first()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return token
    }

    fun getRefreshToken(): String? {
        var token: String? = null
        try {
            runBlocking {
                context.dataStore.data.map { preferences ->
                    preferences[refreshTokenKey]
                }.let {
                    token = it.first()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return token
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[accessTokenKey]
        }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}