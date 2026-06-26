package com.alirahimi.flashcard.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    Dark,
    Light,
    Custom
}

data class AppColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val borderColor: Color,
    val cardBackground: Color
)

val DarkColors = AppColors(
    background = Color(0xFF0F111A),
    surface = Color(0xFF1B1E36),
    primary = Color(0xFF7C4DFF),
    accent = Color(0xFF10B981),
    textPrimary = Color.White,
    textSecondary = Color(0xFFA5B4FC),
    borderColor = Color(0xFF2C3258),
    cardBackground = Color(0xFF1B1E36)
)

val LightColors = AppColors(
    background = Color(0xFFF3F4F6),
    surface = Color(0xFFE5E7EB),
    primary = Color(0xFF6200EE),
    accent = Color(0xFF059669),
    textPrimary = Color(0xFF1F2937),
    textSecondary = Color(0xFF4B5563),
    borderColor = Color(0xFFD1D5DB),
    cardBackground = Color.White
)

data class CustomThemePreset(
    val name: String,
    val color: Color
)

val CustomThemePresets = listOf(
    CustomThemePreset("Amethyst", Color(0xFF8B5CF6)),
    CustomThemePreset("Sunset", Color(0xFFF97316)),
    CustomThemePreset("Rose", Color(0xFFEC4899)),
    CustomThemePreset("Ocean Blue", Color(0xFF06B6D4)),
    CustomThemePreset("Emerald", Color(0xFF10B981)),
    CustomThemePreset("Crimson", Color(0xFFDC2626))
)

fun getCustomColors(primaryColor: Color): AppColors {
    return AppColors(
        background = Color(0xFF0B0F19),
        surface = Color(0xFF141A29),
        primary = primaryColor,
        accent = Color(0xFF10B981),
        textPrimary = Color.White,
        textSecondary = Color(0xFF8EA3C6),
        borderColor = Color(0xFF21293D),
        cardBackground = Color(0xFF141A29)
    )
}

val LocalAppColors = staticCompositionLocalOf { DarkColors }
