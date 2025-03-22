package com.example.simplenote.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(@ApplicationContext private val context: Context) {
    private val _isDarkMode = booleanPreferencesKey("is_dark_mode")

    val isDarkMode: Flow<Boolean?> = context.dataStore.data
        .map { preferences ->
            preferences[_isDarkMode]
        }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[_isDarkMode] = isDarkMode
        }
    }
}
