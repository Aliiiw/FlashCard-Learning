package com.alirahimi.flashcard.feature.cards.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.cards.domain.usecase.AddCardUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AddCardUseCaseTest {

    private lateinit var dataSource: FakeCardsLocalDataSource
    private lateinit var addCardUseCase: AddCardUseCase

    @BeforeTest
    fun setUp() {
        dataSource = FakeCardsLocalDataSource()
        addCardUseCase = AddCardUseCase(dataSource)
    }

    @Test
    fun insertingValidCardSucceedsAndTrimsFields() = runTest {
        val result = addCardUseCase(
            front = "  apple  ",
            back = "  سیب  ",
            language = "EN",
            cardType = "Word",
            currentTimeEpochMs = 1000L
        )

        assertThat(result).isInstanceOf<Result.Success<Unit>>()
        assertThat(dataSource.cards).hasSize(1)
        val card = dataSource.cards.first()
        assertThat(card.front).isEqualTo("apple")
        assertThat(card.back).isEqualTo("سیب")
        assertThat(card.box).isEqualTo(1)
        assertThat(card.nextReviewTimeEpochMs).isEqualTo(1000L)
    }

    @Test
    fun blankFrontReturnsEmptyFrontError() = runTest {
        val result = addCardUseCase("   ", "سیب", "EN", "Word", 1000L)
        assertThat(result).isEqualTo(Result.Error(AddCardError.Validation.EMPTY_FRONT))
    }

    @Test
    fun blankBackReturnsEmptyBackError() = runTest {
        val result = addCardUseCase("apple", "  ", "EN", "Word", 1000L)
        assertThat(result).isEqualTo(Result.Error(AddCardError.Validation.EMPTY_BACK))
    }

    @Test
    fun storageFailureIsWrappedInAddCardErrorStorage() = runTest {
        dataSource.shouldReturnError = true
        val result = addCardUseCase("apple", "سیب", "EN", "Word", 1000L)
        assertThat(result).isEqualTo(Result.Error(AddCardError.Storage(DataError.Local.DISK_FULL)))
    }
}

private class FakeCardsLocalDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()
    var shouldReturnError = false
    private var nextId = 1L

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> {
        return if (shouldReturnError) Result.Error(DataError.Local.UNKNOWN)
        else Result.Success(cards.toList())
    }

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> {
        return Result.Success(cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs })
    }

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> {
        return cards.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.DISK_FULL)
        cards.add(card.copy(id = nextId++))
        return Result.Success(Unit)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> {
        return Result.Success(Unit)
    }

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        cards.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
