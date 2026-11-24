package com.alirahimi.flashcard.feature.review.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.core.domain.onFailure
import com.alirahimi.flashcard.core.domain.onSuccess
import com.alirahimi.flashcard.core.presentation.toUiText
import com.alirahimi.flashcard.feature.review.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.feature.review.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val getCardsForReviewUseCase: GetCardsForReviewUseCase,
    private val reviewCardUseCase: ReviewCardUseCase,
    private val timeProvider: TimeProvider
) : ViewModel() {

    private var cardsToReview: List<FlashCard> = emptyList()

    private val _state = MutableStateFlow(ReviewState())
    val state: StateFlow<ReviewState> = _state.asStateFlow()

    private val _events = Channel<ReviewEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadCards()
    }

    fun onAction(action: ReviewAction) {
        when (action) {
            ReviewAction.OnBackClick -> {
                viewModelScope.launch {
                    _events.send(ReviewEvent.NavigateBack)
                }
            }
            ReviewAction.OnRevealAnswerClick -> {
                _state.update { current ->
                    current.copy(
                        isAnswerVisible = true,
                        displayedAnswer = current.currentCard?.back.orEmpty()
                    )
                }
            }
            is ReviewAction.OnSubmitReview -> submitReview(action.isCorrect)
        }
    }

    private fun loadCards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getCardsForReviewUseCase(timeProvider.currentTimeEpochMs())
                .onSuccess { list ->
                    cardsToReview = list
                    _state.update {
                        ReviewState(
                            currentCard = list.firstOrNull()?.toReviewCardUi(),
                            displayedAnswer = "",
                            currentIndex = 0,
                            totalCards = list.size,
                            isAnswerVisible = false,
                            isFinished = list.isEmpty(),
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val uiText = error.toUiText()
                    _state.update { it.copy(isLoading = false, error = uiText) }
                    _events.send(ReviewEvent.ShowError(uiText))
                }
        }
    }

    private fun submitReview(isCorrect: Boolean) {
        val current = _state.value.currentCard ?: return
        _state.update { it.copy(isAnswerVisible = false) }
        viewModelScope.launch {
            reviewCardUseCase(
                cardId = current.id,
                isCorrect = isCorrect,
                currentTimeEpochMs = timeProvider.currentTimeEpochMs()
            ).onSuccess {
                val nextIndex = _state.value.currentIndex + 1
                if (nextIndex < cardsToReview.size) {
                    _state.update { state ->
                        state.copy(
                            currentIndex = nextIndex,
                            currentCard = cardsToReview[nextIndex].toReviewCardUi(),
                            isAnswerVisible = false
                        )
                    }
                } else {
                    _state.update { state ->
                        state.copy(
                            isFinished = true,
                            currentCard = null,
                            isAnswerVisible = false
                        )
                    }
                }
            }.onFailure { error ->
                val uiText = error.toUiText()
                _state.update { it.copy(error = uiText) }
                _events.send(ReviewEvent.ShowError(uiText))
            }
        }
    }
}
