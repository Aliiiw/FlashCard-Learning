package com.alirahimi.flashcard.feature.importcards.presentation

sealed interface ImportEvent {
    data object NavigateBack : ImportEvent
    data object LaunchFilePicker : ImportEvent
}
