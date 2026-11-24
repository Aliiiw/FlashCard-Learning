package com.alirahimi.flashcard.feature.cards.presentation.list

import com.alirahimi.flashcard.core.presentation.UiText

sealed interface CardListEvent {
    data object NavigateBack : CardListEvent
    data class ShowError(val message: UiText) : CardListEvent
}
