package com.alirahimi.flashcard.core.domain.datasource

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.model.FlashCard

interface CardLocalDataSource {
    suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local>
    suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local>
    suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local>
    suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local>
    suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local>
    suspend fun deleteCard(id: Long): EmptyResult<DataError.Local>
}
