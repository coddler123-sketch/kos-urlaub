package com.example.kosurlaub.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun KosWidgetTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val kos = if (dark) DarkKosColors else LightKosColors
    val scheme = if (dark) {
        darkColorScheme(
            primary = kos.accent, onPrimary = kos.onAccent,
            background = kos.bg, onBackground = kos.ink,
            surface = kos.panel, onSurface = kos.ink,
            surfaceVariant = kos.panelHigh, onSurfaceVariant = kos.ink
        )
    } else {
        lightColorScheme(
            primary = kos.accent, onPrimary = kos.onAccent,
            background = kos.bg, onBackground = kos.ink,
            surface = kos.panel, onSurface = kos.ink,
            surfaceVariant = kos.panelHigh, onSurfaceVariant = kos.ink
        )
    }
    CompositionLocalProvider(LocalKosColors provides kos) {
        MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
    }
}
