package com.alirahimi.flashcard.feature.cards.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.core.domain.onFailure
import com.alirahimi.flashcard.core.domain.onSuccess
import com.alirahimi.flashcard.core.presentation.toUiText
import com.alirahimi.flashcard.feature.cards.domain.usecase.DeleteCardUseCase
import com.alirahimi.flashcard.feature.cards.domain.usecase.GetAllCardsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CardListViewModel(
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val deleteCardUseCase: DeleteCardUseCase
) : ViewModel() {

    private var allCards: List<FlashCard> = emptyList()

    private val _state = MutableStateFlow(CardListState())
    val state: StateFlow<CardListState> = _state.asStateFlow()

    private val _events = Channel<CardListEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadCards()
    }

    fun onAction(action: CardListAction) {
        when (action) {
            CardListAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(CardListEvent.NavigateBack)
                }
            }
            CardListAction.OnRefresh -> loadCards()
            is CardListAction.OnSearchQueryChange -> {
                _state.update { current ->
                    val updated = current.copy(searchQuery = action.query)
                    updated.copy(filteredCards = applyFilters(updated))
                }
            }
            is CardListAction.OnLanguageFilterSelect -> {
                _state.update { current ->
                    val updated = current.copy(selectedLanguageFilter = action.language)
                    updated.copy(filteredCards = applyFilters(updated))
                }
            }
            is CardListAction.OnBoxFilterSelect -> {
                _state.update { current ->
                    val updated = current.copy(selectedBoxFilter = action.box)
                    updated.copy(filteredCards = applyFilters(updated))
                }
            }
            is CardListAction.OnDeleteCardClick -> deleteCard(action.cardId)
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getAllCardsUseCase()
                .onSuccess { cards ->
                    allCards = cards
                    _state.update { current ->
                        current.copy(
                            filteredCards = applyFilters(current),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update { it.copy(isLoading = false, error = uiText) }
                    _events.send(CardListEvent.ShowError(uiText))
                }
        }
    }

    private fun deleteCard(id: Long) {
        viewModelScope.launch {
            deleteCardUseCase(id)
                .onSuccess {
                    allCards = allCards.filterNot { it.id == id }
                    _state.update { current ->
                        current.copy(filteredCards = applyFilters(current))
                    }
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update { it.copy(error = uiText) }
                    _events.send(CardListEvent.ShowError(uiText))
                }
        }
    }

    private fun applyFilters(current: CardListState): List<FlashCardUi> {
        return allCards.filter { card ->
            val matchesQuery = card.front.contains(current.searchQuery, ignoreCase = true) ||
                card.back.contains(current.searchQuery, ignoreCase = true)
            val matchesLanguage = current.selectedLanguageFilter == "ALL" ||
                card.language.equals(current.selectedLanguageFilter, ignoreCase = true)
            val matchesBox = current.selectedBoxFilter == null || card.box == current.selectedBoxFilter
            matchesQuery && matchesLanguage && matchesBox
        }.map { it.toFlashCardUi() }
    }
}
