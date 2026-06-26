package com.alirahimi.flashcard.data.repository

import com.alirahimi.flashcard.db.FlashCardDatabase
import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository
import com.alirahimi.flashcard.db.CardEntity

class CardRepositoryImpl(private val database: FlashCardDatabase) : CardRepository {
    private val queries = database.flashCardDatabaseQueries

    override suspend fun getAllCards(): List<FlashCard> {
        return queries.selectAllCards().executeAsList().map { it.toFlashCard() }
    }

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): List<FlashCard> {
        return queries.selectCardsForReview(currentTimeEpochMs).executeAsList().map { it.toFlashCard() }
    }

    override suspend fun getCardById(id: Long): FlashCard? {
        return queries.selectCardById(id).executeAsOneOrNull()?.toFlashCard()
    }

    override suspend fun insertCard(card: FlashCard) {
        queries.insertCard(
            front = card.front,
            back = card.back,
            language = card.language,
            box = card.box.toLong(),
            nextReviewTimeEpochMs = card.nextReviewTimeEpochMs,
            createdAtEpochMs = card.createdAtEpochMs,
            cardType = card.cardType
        )
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long) {
        queries.updateCardReview(box = box.toLong(), nextReviewTimeEpochMs = nextReviewTimeEpochMs, id = id)
    }

    override suspend fun deleteCard(id: Long) {
        queries.deleteCard(id)
    }

    private fun CardEntity.toFlashCard(): FlashCard {
        return FlashCard(
            id = id,
            front = front,
            back = back,
            language = language,
            box = box.toInt(),
            nextReviewTimeEpochMs = nextReviewTimeEpochMs,
            createdAtEpochMs = createdAtEpochMs,
            cardType = cardType
        )
    }
}
