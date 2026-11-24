package com.alirahimi.flashcard.feature.importcards.domain

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.Error

sealed interface ImportError : Error {
    enum class Validation : ImportError {
        EMPTY_INPUT,
        NO_VALID_LINES
    }

    data class Storage(val error: DataError.Local) : ImportError
}
