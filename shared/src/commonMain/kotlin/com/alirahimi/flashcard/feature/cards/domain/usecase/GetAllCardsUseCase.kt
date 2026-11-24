package com.alirahimi.flashcard.feature.cards.domain.usecase

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard

class GetAllCardsUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(): Result<List<FlashCard>, DataError.Local> {
        return cardLocalDataSource.getAllCards()
    }
}
