package com.alirahimi.flashcard.domain.usecase

import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository

class GetCardsForReviewUseCase(private val repository: CardRepository) {
    suspend operator fun invoke(currentTimeEpochMs: Long): List<FlashCard> {
        return repository.getCardsForReview(currentTimeEpochMs)
    }
}
