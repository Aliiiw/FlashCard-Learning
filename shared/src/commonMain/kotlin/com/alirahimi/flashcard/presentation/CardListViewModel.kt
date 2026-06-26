package com.alirahimi.flashcard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository
import com.alirahimi.flashcard.domain.usecase.GetAllCardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardListViewModel(
    private val getAllCardsUseCase: GetAllCardsUseCase,
    private val repository: CardRepository
) : ViewModel() {
    private val _cards = MutableStateFlow<List<FlashCard>>(emptyList())
    val cards: StateFlow<List<FlashCard>> = _cards

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedLanguageFilter = MutableStateFlow("ALL")
    val selectedLanguageFilter: StateFlow<String> = _selectedLanguageFilter

    private val _selectedBoxFilter = MutableStateFlow<Int?>(null)
    val selectedBoxFilter: StateFlow<Int?> = _selectedBoxFilter

    val filteredCards: StateFlow<List<FlashCard>> = combine(
        _cards,
        _searchQuery,
        _selectedLanguageFilter,
        _selectedBoxFilter
    ) { cardsList, query, language, box ->
        cardsList.filter { card ->
            val matchesQuery = card.front.contains(query, ignoreCase = true) ||
                    card.back.contains(query, ignoreCase = true)
            val matchesLanguage =
                language == "ALL" || card.language.equals(language, ignoreCase = true)
            val matchesBox = box == null || card.box == box
            matchesQuery && matchesLanguage && matchesBox
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadCards() {
        viewModelScope.launch {
            _cards.value = getAllCardsUseCase()
        }
    }

    fun onSearchQueryChanged(value: String) {
        _searchQuery.value = value
    }

    fun onLanguageFilterChanged(value: String) {
        _selectedLanguageFilter.value = value
    }

    fun onBoxFilterChanged(value: Int?) {
        _selectedBoxFilter.value = value
    }

    fun deleteCard(id: Long) {
        viewModelScope.launch {
            repository.deleteCard(id)
            loadCards()
        }
    }
}
