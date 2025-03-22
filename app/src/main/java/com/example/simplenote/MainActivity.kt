package com.example.simplenote

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.data.local.ThemePreferences
import com.example.simplenote.ui.navigation.AppNavHost
import com.example.simplenote.ui.theme.SimpleNoteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemDarkMode =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                        Configuration.UI_MODE_NIGHT_YES)
            val storedDarkTheme by themePreferences.isDarkMode.collectAsState(initial = null)
            val darkTheme = storedDarkTheme ?: systemDarkMode

            SimpleNoteTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
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
