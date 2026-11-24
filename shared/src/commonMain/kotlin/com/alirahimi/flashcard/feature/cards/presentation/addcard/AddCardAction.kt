package com.alirahimi.flashcard.feature.cards.presentation.addcard

sealed interface AddCardAction {
    data object OnBackClick : AddCardAction
    data class OnWordChange(val value: String) : AddCardAction
    data class OnTranslationChange(val value: String) : AddCardAction
    data class OnLanguageSelect(val language: String) : AddCardAction
    data class OnCardTypeSelect(val cardType: String) : AddCardAction
    data object OnSaveClick : AddCardAction
}
