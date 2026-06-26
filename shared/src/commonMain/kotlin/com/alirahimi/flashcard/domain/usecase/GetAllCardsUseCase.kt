package com.alirahimi.flashcard.domain.usecase

import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository

class GetAllCardsUseCase(private val repository: CardRepository) {
    suspend operator fun invoke(): List<FlashCard> {
        return repository.getAllCards()
    }
}
