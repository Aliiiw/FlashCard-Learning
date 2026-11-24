package com.alirahimi.flashcard.feature.cards.presentation.addcard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.onFailure
import com.alirahimi.flashcard.core.domain.onSuccess
import com.alirahimi.flashcard.feature.cards.domain.usecase.AddCardUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCardViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val addCardUseCase: AddCardUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private val _state = MutableStateFlow(
        AddCardState(
            word = savedStateHandle[KEY_WORD] ?: "",
            translation = savedStateHandle[KEY_TRANSLATION] ?: "",
            language = savedStateHandle[KEY_LANGUAGE] ?: "EN",
            cardType = savedStateHandle[KEY_CARD_TYPE] ?: "Word"
        )
    )
    val state: StateFlow<AddCardState> = _state.asStateFlow()

    private val _events = Channel<AddCardEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddCardAction) {
        when (action) {
            AddCardAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(AddCardEvent.NavigateBack)
                }
            }
            is AddCardAction.OnWordChange -> {
                savedStateHandle[KEY_WORD] = action.value
                _state.update { it.copy(word = action.value, error = null) }
            }
            is AddCardAction.OnTranslationChange -> {
                savedStateHandle[KEY_TRANSLATION] = action.value
                _state.update { it.copy(translation = action.value, error = null) }
            }
            is AddCardAction.OnLanguageSelect -> {
                savedStateHandle[KEY_LANGUAGE] = action.language
                _state.update { it.copy(language = action.language) }
            }
            is AddCardAction.OnCardTypeSelect -> {
                savedStateHandle[KEY_CARD_TYPE] = action.cardType
                _state.update { it.copy(cardType = action.cardType) }
            }
            AddCardAction.OnSaveClick -> saveCard()
        }
    }

    private fun saveCard() {
        val currentState = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            addCardUseCase(
                front = currentState.word,
                back = currentState.translation,
                language = currentState.language,
                cardType = currentState.cardType,
                currentTimeEpochMs = timeProvider.currentTimeEpochMs()
            ).onSuccess {
                savedStateHandle[KEY_WORD] = ""
                savedStateHandle[KEY_TRANSLATION] = ""
                _state.update {
                    it.copy(
                        word = "",
                        translation = "",
                        isSaving = false,
                        error = null
                    )
                }
                _events.send(AddCardEvent.CardSaved)
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = error.toUiText()
                    )
                }
            }
        }
    }

    private companion object {
        const val KEY_WORD = "add_card_word"
        const val KEY_TRANSLATION = "add_card_translation"
        const val KEY_LANGUAGE = "add_card_language"
        const val KEY_CARD_TYPE = "add_card_card_type"
    }
}
