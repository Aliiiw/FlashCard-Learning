package com.alirahimi.flashcard.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemeMode {
    Dark,
    Light,
    Custom
}

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

data class ThemeConfig(
    val mode: ThemeMode = ThemeMode.Dark,
    val customPrimaryColor: Color = CustomThemePresets.first().color
)

class ThemeController(
    private val themePreferences: ThemePreferencesDataSource? = null
) {
    private val _themeConfig = MutableStateFlow(loadSavedConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        themePreferences?.saveThemeMode(mode.name)
        _themeConfig.update { it.copy(mode = mode) }
    }

    fun setCustomPrimaryColor(color: Color) {
        themePreferences?.saveThemeMode(ThemeMode.Custom.name)
        themePreferences?.saveCustomColorArgb(color.toArgb())
        _themeConfig.update {
            it.copy(
                mode = ThemeMode.Custom,
                customPrimaryColor = color
            )
        }
    }

    private fun loadSavedConfig(): ThemeConfig {
        val savedMode = themePreferences?.getThemeMode()
            ?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } }
            ?: ThemeMode.Dark
        val savedColor = themePreferences?.getCustomColorArgb()
            ?.let { Color(it) }
            ?: CustomThemePresets.first().color
        return ThemeConfig(
            mode = savedMode,
            customPrimaryColor = savedColor
        )
    }
}

private val DarkAppColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF7C4DFF),
    onPrimary = Color.White,
    secondary = Color(0xFF10B981),
    onSecondary = Color.White,
    background = Color(0xFF0F111A),
    onBackground = Color.White,
    surface = Color(0xFF1B1E36),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B1E36),
    onSurfaceVariant = Color(0xFFA5B4FC),
    outline = Color(0xFF2C3258),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightAppColorScheme: ColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF059669),
    onSecondary = Color.White,
    background = Color(0xFFF3F4F6),
    onBackground = Color(0xFF1F2937),
    surface = Color(0xFFE5E7EB),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color.White,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFD1D5DB),
    error = Color(0xFFEF4444),
    onError = Color.White
)

private fun customAppColorScheme(primaryColor: Color): ColorScheme = darkColorScheme(
    primary = primaryColor,
    onPrimary = Color.White,
    secondary = lerp(primaryColor, Color.White, 0.25f),
    onSecondary = Color.White,
    background = lerp(Color(0xFF090B10), primaryColor, 0.16f),
    onBackground = Color.White,
    surface = lerp(Color(0xFF121520), primaryColor, 0.24f),
    onSurface = Color.White,
    surfaceVariant = lerp(Color(0xFF151926), primaryColor, 0.26f),
    onSurfaceVariant = lerp(Color(0xFFD6DCE8), primaryColor, 0.35f),
    outline = lerp(Color(0xFF2A3142), primaryColor, 0.45f),
    error = Color(0xFFEF4444),
    onError = Color.White
)

@Composable
fun AppTheme(
    themeConfig: ThemeConfig = ThemeConfig(),
    content: @Composable () -> Unit
) {
    val isDarkTheme = themeConfig.mode != ThemeMode.Light
    SystemBarsAppearance(isDarkTheme = isDarkTheme)

    val colorScheme = when (themeConfig.mode) {
        ThemeMode.Dark -> DarkAppColorScheme
        ThemeMode.Light -> LightAppColorScheme
        ThemeMode.Custom -> customAppColorScheme(themeConfig.customPrimaryColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
