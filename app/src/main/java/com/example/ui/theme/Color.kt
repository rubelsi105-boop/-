package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Rubel AI Signature Neon Pink & Cyan Cyberpunk Palette
val NeonPink = Color(0xFFFF2E93)
val NeonPinkLight = Color(0xFFFF69B4)
val NeonMagenta = Color(0xFFD900FF)
val DeepPink = Color(0xFFC70067)

val NeonCyan = Color(0xFF00F0FF)
val ElectricBlue = Color(0xFF0077FE)
val CyanAccent = Color(0xFF38E5FF)

val MidnightBlack = Color(0xFF030514)
val CyberDark = Color(0xFF070C22)
val CyberNavy = Color(0xFF0D1536)
val CyberCardBg = Color(0x990E183D)
val CyberCardBgSolid = Color(0xFF111C47)
val CyberSurface = Color(0xFF131D4A)

val TextWhite = Color(0xFFFFFFFF)
val TextSilver = Color(0xFFB5C3DF)
val TextMuted = Color(0xFF6E7E9E)

// Gradient Brushes
val PinkCyanGlowBrush = Brush.linearGradient(
    colors = listOf(NeonPink, NeonMagenta, ElectricBlue, NeonCyan)
)

val CyanPinkBorderBrush = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonMagenta, NeonPink)
)

val PinkBorderBrush = Brush.horizontalGradient(
    colors = listOf(NeonPink, NeonMagenta, NeonPinkLight)
)

val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF030514),
        Color(0xFF060B24),
        Color(0xFF040718)
    )
)
