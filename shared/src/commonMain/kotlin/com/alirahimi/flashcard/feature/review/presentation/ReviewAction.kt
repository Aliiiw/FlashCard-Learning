package com.alirahimi.flashcard.feature.review.presentation

sealed interface ReviewAction {
    data object OnBackClick : ReviewAction
    data object OnRevealAnswerClick : ReviewAction
    data class OnSubmitReview(val isCorrect: Boolean) : ReviewAction
}
