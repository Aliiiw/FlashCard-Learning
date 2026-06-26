package com.alirahimi.flashcard.domain.repository

import com.alirahimi.flashcard.domain.model.FlashCard

interface CardRepository {
    suspend fun getAllCards(): List<FlashCard>
    suspend fun getCardsForReview(currentTimeEpochMs: Long): List<FlashCard>
    suspend fun getCardById(id: Long): FlashCard?
    suspend fun insertCard(card: FlashCard)
    suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long)
    suspend fun deleteCard(id: Long)
}
