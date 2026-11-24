package com.alirahimi.flashcard.feature.dashboard.presentation

import androidx.compose.ui.graphics.Color
import com.alirahimi.flashcard.core.designsystem.theme.ThemeMode

sealed interface DashboardAction {
    data object OnRefresh : DashboardAction
    data object OnStartReviewClick : DashboardAction
    data object OnAddCardClick : DashboardAction
    data object OnWordListClick : DashboardAction
    data object OnImportClick : DashboardAction
    data object OnOpenThemeDialog : DashboardAction
    data object OnDismissThemeDialog : DashboardAction
    data class OnThemeModeSelect(val mode: ThemeMode) : DashboardAction
    data class OnCustomColorSelect(val color: Color) : DashboardAction
}
