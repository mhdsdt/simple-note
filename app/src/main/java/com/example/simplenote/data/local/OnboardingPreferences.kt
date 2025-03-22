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

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "onboarding_settings"
)

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingCompleted = booleanPreferencesKey("onboarding_completed")

    val isOnboardingCompleted: Flow<Boolean?> =
        context.onboardingDataStore.data.map { preferences ->
            preferences[onboardingCompleted]
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.onboardingDataStore.edit { preferences ->
            preferences[onboardingCompleted] = completed
        }
    }
}
