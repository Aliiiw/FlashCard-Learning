package com.alirahimi.flashcard.core.data.datasource

import android.content.Context
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource

class AndroidThemePreferencesDataSource(
    context: Context
) : ThemePreferencesDataSource {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getThemeMode(): String? = prefs.getString(KEY_THEME_MODE, null)

    override fun saveThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
    }

    override fun getCustomColorArgb(): Int? {
        return if (prefs.contains(KEY_CUSTOM_COLOR)) {
            prefs.getInt(KEY_CUSTOM_COLOR, 0)
        } else {
            null
        }
    }

    override fun saveCustomColorArgb(colorArgb: Int) {
        prefs.edit().putInt(KEY_CUSTOM_COLOR, colorArgb).apply()
    }

    private companion object {
        const val PREFS_NAME = "flashcard_theme_prefs"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_CUSTOM_COLOR = "custom_color_argb"
    }
}
