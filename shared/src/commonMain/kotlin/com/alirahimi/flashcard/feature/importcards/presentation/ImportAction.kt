package com.alirahimi.flashcard.feature.importcards.presentation

sealed interface ImportAction {
    data object OnBackClick : ImportAction
    data class OnInputTextChange(val text: String) : ImportAction
    data class OnLanguageSelect(val language: String) : ImportAction
    data class OnCardTypeSelect(val cardType: String) : ImportAction
    data object OnPickFileClick : ImportAction
    data class OnFileContentLoaded(val content: String) : ImportAction
    data object OnImportClick : ImportAction
    data object OnClearTextClick : ImportAction
}
