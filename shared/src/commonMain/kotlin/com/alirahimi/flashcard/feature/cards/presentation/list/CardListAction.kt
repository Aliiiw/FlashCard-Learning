package com.alirahimi.flashcard.feature.cards.presentation.list

sealed interface CardListAction {
    data object OnBackClick : CardListAction
    data object OnRefresh : CardListAction
    data class OnSearchQueryChange(val query: String) : CardListAction
    data class OnLanguageFilterSelect(val language: String) : CardListAction
    data class OnBoxFilterSelect(val box: Int?) : CardListAction
    data class OnDeleteCardClick(val cardId: Long) : CardListAction
}
