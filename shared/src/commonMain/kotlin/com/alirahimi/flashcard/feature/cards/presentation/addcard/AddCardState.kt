package com.alirahimi.flashcard.feature.cards.presentation.addcard

import com.alirahimi.flashcard.core.presentation.UiText

data class AddCardState(
    val word: String = "",
    val translation: String = "",
    val language: String = "EN",
    val cardType: String = "Word",
    val isSaving: Boolean = false,
    val error: UiText? = null
)
