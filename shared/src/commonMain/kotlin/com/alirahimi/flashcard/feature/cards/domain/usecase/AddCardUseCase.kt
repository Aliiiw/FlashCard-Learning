package com.alirahimi.flashcard.feature.cards.domain.usecase

import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.cards.domain.AddCardError

class AddCardUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(
        front: String,
        back: String,
        language: String,
        cardType: String,
        currentTimeEpochMs: Long
    ): EmptyResult<AddCardError> {
        if (front.isBlank()) {
            return Result.Error(AddCardError.Validation.EMPTY_FRONT)
        }
        if (back.isBlank()) {
            return Result.Error(AddCardError.Validation.EMPTY_BACK)
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
        return when (val result = cardLocalDataSource.insertCard(card)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> Result.Error(AddCardError.Storage(result.error))
        }
    }
}
