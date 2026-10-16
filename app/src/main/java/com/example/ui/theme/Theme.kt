package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = DeepNavy,
    primaryContainer = CardNavyElevated,
    onPrimaryContainer = CyberCyan,
    secondary = TechGold,
    onSecondary = DeepNavy,
    secondaryContainer = CardNavyElevated,
    onSecondaryContainer = TechGold,
    tertiary = TechEmerald,
    onTertiary = DeepNavy,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = CardNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavyElevated,
    onSurfaceVariant = TextSecondary,
    outline = BorderNavy,
    error = TechCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We enforce the sleek tech dark command center scheme for consistency
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
