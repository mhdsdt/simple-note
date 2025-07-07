package com.example.simplenote.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBase,
    secondary = SecondaryBase,
    tertiary = PrimaryDark,
    background = PrimaryBackground,
    surface = NeutralWhite,
    error = ErrorBase,
    onPrimary = NeutralWhite,
    onSecondary = NeutralWhite,
    onTertiary = NeutralWhite,
    onBackground = NeutralBlack,
    onSurface = NeutralBlack,
    onError = NeutralWhite,
    secondaryContainer = NoteColor,
    onSecondaryContainer = NeutralBlack,
    surfaceVariant = NeutralLightGrey
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = PrimaryBase,
    background = DarkBackground,
    surface = DarkSurface,
    error = ErrorBase,
    onPrimary = NeutralBlack,
    onSecondary = NeutralBlack,
    onTertiary = NeutralBlack,
    onBackground = NeutralWhite,
    onSurface = NeutralWhite,
    onError = NeutralBlack,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = NeutralWhite,
    surfaceVariant = DarkSurfaceVariant
)

@Composable
fun SimpleNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
