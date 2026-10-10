
package com.example.comunikt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF087F8C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEAF5F6),
    onPrimaryContainer = Color(0xFF17324D),

    secondary = Color(0xFF17324D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEAF5F6),
    onSecondaryContainer = Color(0xFF17324D),

    background = Color(0xFFF4F7FA),
    onBackground = Color(0xFF17324D),
    surface = Color.White,
    onSurface = Color(0xFF17324D),
    surfaceVariant = Color(0xFFEAF5F6),
    onSurfaceVariant = Color(0xFF536477),

    outline = Color(0xFFB9C9D4),
    outlineVariant = Color(0xFFDDE5ED),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4DB9C2),
    onPrimary = Color(0xFF10222B),
    primaryContainer = Color(0xFF195662),
    onPrimaryContainer = Color(0xFFDDF4F5),

    secondary = Color(0xFF274B65),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF243C4F),
    onSecondaryContainer = Color(0xFFE4F0F7),

    background = Color(0xFF101B29),
    onBackground = Color(0xFFF0F7FC),
    surface = Color(0xFF1B3042),
    onSurface = Color(0xFFF0F7FC),
    surfaceVariant = Color(0xFF243C4F),
    onSurfaceVariant = Color(0xFFBDCDDA),

    outline = Color(0xFF748E9D),
    outlineVariant = Color(0xFF385367),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun ComuniKTTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
