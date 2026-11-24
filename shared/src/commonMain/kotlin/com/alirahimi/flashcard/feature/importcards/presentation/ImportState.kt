package com.alirahimi.flashcard.feature.importcards.presentation

import com.alirahimi.flashcard.core.presentation.UiText

data class ImportSummaryUi(
    val importedCount: Int,
    val skippedCount: Int
)

data class ImportState(
    val inputText: String = "",
    val language: String = "EN",
    val cardType: String = "Word",
    val isImporting: Boolean = false,
    val summary: ImportSummaryUi? = null,
    val error: UiText? = null
)
