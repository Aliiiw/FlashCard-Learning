package com.alirahimi.flashcard.feature.cards.presentation.addcard

import com.alirahimi.flashcard.core.presentation.UiText
import com.alirahimi.flashcard.core.presentation.resources.Res
import com.alirahimi.flashcard.core.presentation.resources.error_empty_back
import com.alirahimi.flashcard.core.presentation.resources.error_empty_front
import com.alirahimi.flashcard.core.presentation.toUiText
import com.alirahimi.flashcard.feature.cards.domain.AddCardError

fun AddCardError.toUiText(): UiText {
    return when (this) {
        AddCardError.Validation.EMPTY_FRONT -> UiText.StringResourceId(Res.string.error_empty_front)
        AddCardError.Validation.EMPTY_BACK -> UiText.StringResourceId(Res.string.error_empty_back)
        is AddCardError.Storage -> error.toUiText()
    }
}
