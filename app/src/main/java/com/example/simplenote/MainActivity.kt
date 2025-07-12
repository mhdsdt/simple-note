package com.example.simplenote

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.data.local.ThemePreferences
import com.example.simplenote.ui.navigation.AppNavHost
import com.example.simplenote.ui.theme.SimpleNoteTheme
import com.example.simplenote.viewmodel.SessionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    private val sessionViewModel: SessionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkMode =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                        Configuration.UI_MODE_NIGHT_YES)
            val storedDarkTheme by themePreferences.isDarkMode.collectAsState(initial = null)
            val darkTheme = storedDarkTheme ?: systemDarkMode

            val uiState by sessionViewModel.uiState.collectAsState()

            SimpleNoteTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // If the state is loading, show a progress indicator.
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Otherwise, show the AppNavHost with the now-confirmed state.
                        val navController = rememberNavController()
                        AppNavHost(
                            navController = navController,
                            isLoggedIn = uiState.isLoggedIn,
                            onboardingCompleted = uiState.onboardingCompleted,
                            isDarkMode = darkTheme,
                            onThemeChanged = { newDarkModeValue ->
                                lifecycleScope.launch {
                                    themePreferences.setDarkMode(newDarkModeValue)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
