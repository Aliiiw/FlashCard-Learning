package com.alirahimi.flashcard.feature.importcards.presentation

import com.alirahimi.flashcard.core.presentation.UiText
import com.alirahimi.flashcard.core.presentation.resources.Res
import com.alirahimi.flashcard.core.presentation.resources.error_import_empty
import com.alirahimi.flashcard.core.presentation.resources.error_import_no_valid_lines
import com.alirahimi.flashcard.core.presentation.toUiText
import com.alirahimi.flashcard.feature.importcards.domain.ImportError

fun ImportError.toUiText(): UiText {
    return when (this) {
        ImportError.Validation.EMPTY_INPUT -> UiText.StringResourceId(Res.string.error_import_empty)
        ImportError.Validation.NO_VALID_LINES -> UiText.StringResourceId(Res.string.error_import_no_valid_lines)
        is ImportError.Storage -> error.toUiText()
    }
}
