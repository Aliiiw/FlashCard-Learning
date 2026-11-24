package com.alirahimi.flashcard.feature.review.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.review.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.feature.review.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ReviewUseCasesTest {

    private lateinit var dataSource: FakeReviewLocalDataSource
    private lateinit var getCardsForReviewUseCase: GetCardsForReviewUseCase
    private lateinit var reviewCardUseCase: ReviewCardUseCase

    @BeforeTest
    fun setUp() {
        dataSource = FakeReviewLocalDataSource()
        getCardsForReviewUseCase = GetCardsForReviewUseCase(dataSource)
        reviewCardUseCase = ReviewCardUseCase(dataSource)
    }

    @Test
    fun correctReviewAdvancesBoxAndSchedulesExponentialInterval() = runTest {
        dataSource.insertCard(FlashCard(0L, "apple", "سیب", "EN", 1, 1000L, 1000L, "Word"))
        val cardId = dataSource.cards.first().id

        val result = reviewCardUseCase(cardId, isCorrect = true, currentTimeEpochMs = 2000L)

        assertThat(result).isInstanceOf<Result.Success<Unit>>()
        val updated = dataSource.cards.first()
        assertThat(updated.box).isEqualTo(2)
        assertThat(updated.nextReviewTimeEpochMs).isEqualTo(2000L + 2L * 24 * 60 * 60 * 1000)
    }

    @Test
    fun incorrectReviewResetsBoxToOne() = runTest {
        dataSource.insertCard(FlashCard(0L, "apple", "سیب", "EN", 4, 1000L, 1000L, "Word"))
        val cardId = dataSource.cards.first().id

        reviewCardUseCase(cardId, isCorrect = false, currentTimeEpochMs = 2000L)

        val updated = dataSource.cards.first()
        assertThat(updated.box).isEqualTo(1)
        assertThat(updated.nextReviewTimeEpochMs).isEqualTo(2000L + 1L * 24 * 60 * 60 * 1000)
    }

    @Test
    fun reviewMissingCardReturnsNotFoundError() = runTest {
        val result = reviewCardUseCase(cardId = 999L, isCorrect = true, currentTimeEpochMs = 2000L)
        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun getCardsForReviewFiltersDueCardsOnly() = runTest {
        dataSource.insertCard(FlashCard(0L, "c1", "b1", "EN", 1, 1000L, 500L, "Word"))
        dataSource.insertCard(FlashCard(0L, "c2", "b2", "EN", 1, 3000L, 500L, "Word"))

        val result = getCardsForReviewUseCase(2000L)
        assertThat(result).isInstanceOf<Result.Success<List<FlashCard>>>()
        val cards = (result as Result.Success).data
        assertThat(cards).hasSize(1)
        assertThat(cards.first().front).isEqualTo("c1")
    }
}

private class FakeReviewLocalDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()
    private var nextId = 1L

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.toList())

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs })

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> =
        cards.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        cards.add(card.copy(id = nextId++))
        return Result.Success(Unit)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> {
        val idx = cards.indexOfFirst { it.id == id }
        if (idx == -1) return Result.Error(DataError.Local.NOT_FOUND)
        cards[idx] = cards[idx].copy(box = box, nextReviewTimeEpochMs = nextReviewTimeEpochMs)
        return Result.Success(Unit)
    }

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        cards.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
