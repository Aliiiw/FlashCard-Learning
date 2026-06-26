package com.alirahimi.flashcard.domain

import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository

class FakeCardRepository : CardRepository {
    private val cards = mutableListOf<FlashCard>()
    private var nextId = 1L

    override suspend fun getAllCards(): List<FlashCard> {
        return cards.toList()
    }

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): List<FlashCard> {
        return cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs }
    }

    override suspend fun getCardById(id: Long): FlashCard? {
        return cards.find { it.id == id }
    }

    override suspend fun insertCard(card: FlashCard) {
        val cardWithId = if (card.id == 0L) {
            card.copy(id = nextId++)
        } else {
            card
        }
        cards.add(cardWithId)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long) {
        val index = cards.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = cards[index]
            cards[index] = existing.copy(box = box, nextReviewTimeEpochMs = nextReviewTimeEpochMs)
        }
    }

    override suspend fun deleteCard(id: Long) {
        cards.removeAll { it.id == id }
    }
}
