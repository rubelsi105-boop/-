package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RubelCyberColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = Color.White,
    primaryContainer = DeepPink,
    onPrimaryContainer = Color.White,
    secondary = NeonCyan,
    onSecondary = MidnightBlack,
    secondaryContainer = CyberNavy,
    onSecondaryContainer = CyanAccent,
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = MidnightBlack,
    onBackground = TextWhite,
    surface = CyberDark,
    onSurface = TextWhite,
    surfaceVariant = CyberNavy,
    onSurfaceVariant = TextSilver,
    outline = NeonPinkLight
)

private val RubelCyberLightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = NeonPink,
    onPrimary = Color.White,
    primaryContainer = DeepPink,
    onPrimaryContainer = Color.White,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE1F5FE),
    onSecondaryContainer = Color(0xFF01579B),
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = Color(0xFF0F172A),
    onBackground = TextWhite,
    surface = Color(0xFF1E293B),
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextSilver,
    outline = NeonCyan
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RubelCyberColorScheme else RubelCyberLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
