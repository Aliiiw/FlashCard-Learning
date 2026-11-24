package com.alirahimi.flashcard.feature.cards.presentation.addcard

sealed interface AddCardEvent {
    data object NavigateBack : AddCardEvent
    data object CardSaved : AddCardEvent
}
