package com.alirahimi.flashcard.feature.cards.domain

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.Error

sealed interface AddCardError : Error {
    enum class Validation : AddCardError {
        EMPTY_FRONT,
        EMPTY_BACK
    }

    data class Storage(val error: DataError.Local) : AddCardError
}
