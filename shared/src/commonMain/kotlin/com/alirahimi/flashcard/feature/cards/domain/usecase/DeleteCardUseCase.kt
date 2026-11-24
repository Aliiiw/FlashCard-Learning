package com.alirahimi.flashcard.feature.cards.domain.usecase

import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource

class DeleteCardUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(id: Long): EmptyResult<DataError.Local> {
        return cardLocalDataSource.deleteCard(id)
    }
}
