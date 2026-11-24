package com.alirahimi.flashcard.core.domain.datasource

interface ThemePreferencesDataSource {
    fun getThemeMode(): String?
    fun saveThemeMode(mode: String)
    fun getCustomColorArgb(): Int?
    fun saveCustomColorArgb(colorArgb: Int)
}
