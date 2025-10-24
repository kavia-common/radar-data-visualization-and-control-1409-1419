package org.example.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OceanColorScheme: ColorScheme = lightColorScheme(
    primary = OceanPrimary,
    secondary = OceanSecondary,
    background = OceanBackground,
    surface = OceanSurface,
    error = OceanError,
    onPrimary = OceanSurface,
    onSecondary = OceanText,
    onBackground = OceanText,
    onSurface = OceanText,
    onError = OceanSurface
)

// PUBLIC_INTERFACE
@Composable
fun OceanProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OceanColorScheme,
        typography = Typography(),
        content = content
    )
}
