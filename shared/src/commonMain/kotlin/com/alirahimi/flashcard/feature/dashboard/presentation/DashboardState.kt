package com.alirahimi.flashcard.feature.dashboard.presentation

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.alirahimi.flashcard.core.designsystem.theme.CustomThemePresets
import com.alirahimi.flashcard.core.designsystem.theme.ThemeMode
import com.alirahimi.flashcard.core.presentation.UiText

data class BoxSummaryUi(
    val boxNumber: Int,
    val title: String,
    val count: Int,
    val subtitle: String
)

@Stable
data class DashboardState(
    val boxes: List<BoxSummaryUi> = defaultBoxes(),
    val dueCardsCount: Int = 0,
    val themeMode: ThemeMode = ThemeMode.Dark,
    val customPrimaryColor: Color = CustomThemePresets.first().color,
    val isThemeDialogVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
) {
    companion object {
        fun defaultBoxes(counts: Map<Int, Int> = emptyMap()): List<BoxSummaryUi> = listOf(
            BoxSummaryUi(1, "Box 1", counts[1] ?: 0, "Every 1 Day"),
            BoxSummaryUi(2, "Box 2", counts[2] ?: 0, "Every 2 Days"),
            BoxSummaryUi(3, "Box 3", counts[3] ?: 0, "Every 4 Days"),
            BoxSummaryUi(4, "Box 4", counts[4] ?: 0, "Every 8 Days"),
            BoxSummaryUi(5, "Box 5", counts[5] ?: 0, "Every 16 Days")
        )
    }
}
