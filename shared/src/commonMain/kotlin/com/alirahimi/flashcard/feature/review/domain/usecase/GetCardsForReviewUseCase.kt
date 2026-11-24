package com.alirahimi.flashcard.feature.review.domain.usecase

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard

class GetCardsForReviewUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> {
        return cardLocalDataSource.getCardsForReview(currentTimeEpochMs)
    }
}
