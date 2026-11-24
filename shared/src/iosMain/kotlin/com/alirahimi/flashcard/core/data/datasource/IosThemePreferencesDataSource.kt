package com.alirahimi.flashcard.core.data.datasource

import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import platform.Foundation.NSUserDefaults

class IosThemePreferencesDataSource : ThemePreferencesDataSource {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getThemeMode(): String? = defaults.stringForKey(KEY_THEME_MODE)

    override fun saveThemeMode(mode: String) {
        defaults.setObject(mode, forKey = KEY_THEME_MODE)
    }

    override fun getCustomColorArgb(): Int? {
        return if (defaults.objectForKey(KEY_CUSTOM_COLOR) != null) {
            defaults.integerForKey(KEY_CUSTOM_COLOR).toInt()
        } else {
            null
        }
    }

    override fun saveCustomColorArgb(colorArgb: Int) {
        defaults.setInteger(colorArgb.toLong(), forKey = KEY_CUSTOM_COLOR)
    }

    private companion object {
        const val KEY_THEME_MODE = "flashcard_theme_mode"
        const val KEY_CUSTOM_COLOR = "flashcard_custom_color_argb"
    }
}
