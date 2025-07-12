package com.example.simplenote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.data.local.OnboardingPreferences
import com.example.simplenote.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// NEW: A state class to hold the entire session UI state
data class UiState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val onboardingCompleted: Boolean = false
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    tokenManager: TokenManager,
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        // Combine the two async flows. The result will emit whenever either one changes.
        viewModelScope.launch {
            combine(
                tokenManager.accessTokenFlow,
                onboardingPreferences.isOnboardingCompleted
            ) { token, onboardingComplete ->
                UiState(
                    isLoading = false, // As soon as we get a result, loading is done
                    isLoggedIn = token != null,
                    onboardingCompleted = onboardingComplete ?: false
                )
            }.collect {
                // Update the single state holder
                _uiState.value = it
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingCompleted(true)
        }
    }
}