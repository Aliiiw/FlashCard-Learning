package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.onFailure
import com.alirahimi.flashcard.core.domain.onSuccess
import com.alirahimi.flashcard.feature.importcards.domain.usecase.ImportCardsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImportViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val importCardsUseCase: ImportCardsUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val _state = MutableStateFlow(
        ImportState(
            inputText = savedStateHandle[KEY_INPUT_TEXT] ?: "",
            language = savedStateHandle[KEY_LANGUAGE] ?: "EN",
            cardType = savedStateHandle[KEY_CARD_TYPE] ?: "Word"
        )
    )
    val state: StateFlow<ImportState> = _state.asStateFlow()

    private val _events = Channel<ImportEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ImportAction) {
        when (action) {
            ImportAction.OnBackClick -> sendEvent(ImportEvent.NavigateBack)
            ImportAction.OnPickFileClick -> sendEvent(ImportEvent.LaunchFilePicker)
            is ImportAction.OnInputTextChange -> updateInputText(action.text)
            is ImportAction.OnFileContentLoaded -> updateInputText(action.content)
            is ImportAction.OnLanguageSelect -> {
                savedStateHandle[KEY_LANGUAGE] = action.language
                _state.update { it.copy(language = action.language, summary = null, error = null) }
            }
            is ImportAction.OnCardTypeSelect -> {
                savedStateHandle[KEY_CARD_TYPE] = action.cardType
                _state.update { it.copy(cardType = action.cardType, summary = null, error = null) }
            }
            ImportAction.OnClearTextClick -> updateInputText("")
            ImportAction.OnImportClick -> importWords()
        }
    }

    private fun updateInputText(value: String) {
        savedStateHandle[KEY_INPUT_TEXT] = value
        _state.update { it.copy(inputText = value, summary = null, error = null) }
    }

    private fun importWords() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isImporting = true, summary = null, error = null) }
            importCardsUseCase(
                rawText = currentState.inputText,
                language = currentState.language,
                cardType = currentState.cardType,
                currentTimeEpochMs = timeProvider.currentTimeEpochMs()
            ).onSuccess { result ->
                _state.update {
                    it.copy(
                        isImporting = false,
                        summary = ImportSummaryUi(
                            importedCount = result.importedCount,
                            skippedCount = result.skippedCount
                        ),
                        error = null
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isImporting = false,
                        summary = null,
                        error = error.toUiText()
                    )
                }
            }
        }
    }

    private fun sendEvent(event: ImportEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    private companion object {
        const val KEY_INPUT_TEXT = "import_input_text"
        const val KEY_LANGUAGE = "import_language"
        const val KEY_CARD_TYPE = "import_card_type"
    }
}
