package com.alirahimi.flashcard.feature.dashboard.presentation

import com.alirahimi.flashcard.core.presentation.UiText

sealed interface DashboardEvent {
    data object NavigateToReview : DashboardEvent
    data object NavigateToAddCard : DashboardEvent
    data object NavigateToCardList : DashboardEvent
    data object NavigateToImport : DashboardEvent
    data class ShowError(val message: UiText) : DashboardEvent
}
