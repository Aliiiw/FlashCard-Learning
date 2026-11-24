package com.alirahimi.flashcard.feature.review.domain.usecase

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource

class ReviewCardUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(
        cardId: Long,
        isCorrect: Boolean,
        currentTimeEpochMs: Long
    ): EmptyResult<DataError.Local> {
        val card = when (val cardResult = cardLocalDataSource.getCardById(cardId)) {
            is Result.Success -> cardResult.data
            is Result.Error -> return Result.Error(cardResult.error)
        }

        val newBox = if (isCorrect) {
            (card.box + 1).coerceAtMost(MAX_BOX)
        } else {
            MIN_BOX
        }

        val intervalMs = when (newBox) {
            1 -> 1L * DAY_MS
            2 -> 2L * DAY_MS
            3 -> 4L * DAY_MS
            4 -> 8L * DAY_MS
            5 -> 16L * DAY_MS
            else -> 1L * DAY_MS
        }

        val nextReviewTime = currentTimeEpochMs + intervalMs
        return cardLocalDataSource.updateCardReview(cardId, newBox, nextReviewTime)
    }

    private companion object {
        const val MIN_BOX = 1
        const val MAX_BOX = 5
        const val DAY_MS = 24L * 60 * 60 * 1000
    }
}
