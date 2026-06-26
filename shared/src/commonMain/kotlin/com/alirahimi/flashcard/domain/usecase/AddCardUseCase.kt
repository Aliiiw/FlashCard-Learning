package com.alirahimi.flashcard.domain.usecase

import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository

class AddCardUseCase(private val repository: CardRepository) {
    suspend operator fun invoke(
        front: String,
        back: String,
        language: String,
        cardType: String,
        currentTimeEpochMs: Long
    ) {
        if (front.isBlank() || back.isBlank()) {
            throw IllegalArgumentException("Fields cannot be empty")
        }
        val card = FlashCard(
            id = 0L,
            front = front.trim(),
            back = back.trim(),
            language = language,
            box = 1,
            nextReviewTimeEpochMs = currentTimeEpochMs,
            createdAtEpochMs = currentTimeEpochMs,
            cardType = cardType
        )
        repository.insertCard(card)
    }
}
