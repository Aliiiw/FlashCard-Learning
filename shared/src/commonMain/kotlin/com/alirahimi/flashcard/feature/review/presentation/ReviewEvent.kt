package com.alirahimi.flashcard.feature.review.presentation

import com.alirahimi.flashcard.core.presentation.UiText

sealed interface ReviewEvent {
    data object NavigateBack : ReviewEvent
    data class ShowError(val message: UiText) : ReviewEvent
}
