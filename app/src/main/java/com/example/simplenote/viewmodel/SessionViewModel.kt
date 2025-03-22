package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.local.TokenManager
import com.example.simplenote.data.local.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    tokenManager: TokenManager,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> =
        tokenManager.accessTokenFlow
            .map { token -> token != null }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = tokenManager.getAccessToken() != null
            )

    val onboardingCompleted: StateFlow<Boolean> =
        onboardingPreferences.isOnboardingCompleted
            .map { it ?: false }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = false
            )

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingCompleted(true)
        }
    }
}
