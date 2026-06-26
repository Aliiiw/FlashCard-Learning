package com.alirahimi.flashcard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val getCardsForReviewUseCase: GetCardsForReviewUseCase,
    private val reviewCardUseCase: ReviewCardUseCase
) : ViewModel() {
    private val _cardsToReview = MutableStateFlow<List<FlashCard>>(emptyList())
    val cardsToReview: StateFlow<List<FlashCard>> = _cardsToReview.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex: StateFlow<Int> = _currentCardIndex.asStateFlow()

    private val _isAnswerVisible = MutableStateFlow(false)
    val isAnswerVisible: StateFlow<Boolean> = _isAnswerVisible.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _currentCard = MutableStateFlow<FlashCard?>(null)
    val currentCard: StateFlow<FlashCard?> = _currentCard.asStateFlow()

    fun loadCards(currentTimeEpochMs: Long) {
        viewModelScope.launch {
            val list = getCardsForReviewUseCase(currentTimeEpochMs)
            _cardsToReview.value = list
            _currentCardIndex.value = 0
            _isAnswerVisible.value = false
            _isFinished.value = list.isEmpty()
            _currentCard.value = list.firstOrNull()
        }
    }

    fun revealAnswer() {
        _isAnswerVisible.value = true
    }

    fun submitReview(isCorrect: Boolean, currentTimeEpochMs: Long) {
        val current = _currentCard.value ?: return
        viewModelScope.launch {
            reviewCardUseCase(current.id, isCorrect, currentTimeEpochMs)
            val nextIndex = _currentCardIndex.value + 1
            if (nextIndex < _cardsToReview.value.size) {
                _currentCardIndex.value = nextIndex
                _isAnswerVisible.value = false
                _currentCard.value = _cardsToReview.value[nextIndex]
            } else {
                _isFinished.value = true
                _currentCard.value = null
            }
        }
    }
}
