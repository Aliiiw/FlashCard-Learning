package com.alirahimi.flashcard.domain.usecase

import com.alirahimi.flashcard.domain.repository.CardRepository

class ReviewCardUseCase(private val repository: CardRepository) {
    suspend operator fun invoke(cardId: Long, isCorrect: Boolean, currentTimeEpochMs: Long) {
        val card = repository.getCardById(cardId) ?: return
        val newBox = if (isCorrect) {
            (card.box + 1).coerceAtMost(5)
        } else {
            1
        }
        val intervalMs = when (newBox) {
            1 -> 1L * 24 * 60 * 60 * 1000
            2 -> 2L * 24 * 60 * 60 * 1000
            3 -> 4L * 24 * 60 * 60 * 1000
            4 -> 8L * 24 * 60 * 60 * 1000
            5 -> 16L * 24 * 60 * 60 * 1000
            else -> 1L * 24 * 60 * 60 * 1000
        }
        val nextReviewTime = currentTimeEpochMs + intervalMs
        repository.updateCardReview(cardId, newBox, nextReviewTime)
    }
}
