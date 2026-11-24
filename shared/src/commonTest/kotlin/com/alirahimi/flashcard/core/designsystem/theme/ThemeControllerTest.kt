package com.alirahimi.flashcard.core.designsystem.theme

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import kotlin.test.Test

class ThemeControllerTest {

    @Test
    fun persistsAndRestoresThemeModeAndCustomColorAcrossRestarts() {
        val prefs = FakeThemePreferencesDataSource()
        val firstSession = ThemeController(prefs)
        val sunsetColor = CustomThemePresets.first { it.name == "Sunset" }.color

        firstSession.setThemeMode(ThemeMode.Custom)
        firstSession.setCustomPrimaryColor(sunsetColor)

        val secondSession = ThemeController(prefs)
        assertThat(secondSession.themeConfig.value.mode).isEqualTo(ThemeMode.Custom)
        assertThat(secondSession.themeConfig.value.customPrimaryColor).isEqualTo(sunsetColor)

        secondSession.setThemeMode(ThemeMode.Light)
        val thirdSession = ThemeController(prefs)
        assertThat(thirdSession.themeConfig.value.mode).isEqualTo(ThemeMode.Light)
        assertThat(thirdSession.themeConfig.value.customPrimaryColor).isEqualTo(sunsetColor)
    }
}

private class FakeThemePreferencesDataSource : ThemePreferencesDataSource {
    private var mode: String? = null
    private var colorArgb: Int? = null

    override fun getThemeMode(): String? = mode

    override fun saveThemeMode(mode: String) {
        this.mode = mode
    }

    override fun getCustomColorArgb(): Int? = colorArgb

    override fun saveCustomColorArgb(colorArgb: Int) {
        this.colorArgb = colorArgb
    }
}
