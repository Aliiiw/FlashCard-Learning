package com.alirahimi.flashcard.feature.importcards.domain

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.importcards.domain.usecase.ImportCardsUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ImportCardsUseCaseTest {

    private lateinit var dataSource: FakeImportLocalDataSource
    private lateinit var importCardsUseCase: ImportCardsUseCase

    @BeforeTest
    fun setUp() {
        dataSource = FakeImportLocalDataSource()
        importCardsUseCase = ImportCardsUseCase(dataSource)
    }

    @Test
    fun importsValidLinesAndSkipsExistingAndIntraBatchDuplicates() = runTest {
        dataSource.insertCard(FlashCard(0L, "appearance", "ظاهر", "EN", 1, 100L, 100L, "Word"))

        val raw = """
            Appearance -> ظاهر
            beauty -> زیبایی
            BEAUTY -> زیبایی تکراری
            tall - بلندقد
            short : کوتاه‌قد
            slim = لاغر
            invalid line without separator
        """.trimIndent()

        val result = importCardsUseCase(raw, language = "EN", cardType = "Word", currentTimeEpochMs = 500L)

        assertThat(result).isInstanceOf<Result.Success<ImportSummary>>()
        val summary = (result as Result.Success).data
        assertThat(summary.importedCount).isEqualTo(4)
        assertThat(summary.skippedCount).isEqualTo(2)
        assertThat(dataSource.cards).hasSize(5)
    }

    @Test
    fun emptyInputReturnsValidationError() = runTest {
        val result = importCardsUseCase("   ", "EN", "Word", 500L)
        assertThat(result).isEqualTo(Result.Error(ImportError.Validation.EMPTY_INPUT))
    }

    @Test
    fun inputWithNoValidSeparatorsReturnsNoValidLinesError() = runTest {
        val result = importCardsUseCase("just random text\nanother line", "EN", "Word", 500L)
        assertThat(result).isEqualTo(Result.Error(ImportError.Validation.NO_VALID_LINES))
    }
}

private class FakeImportLocalDataSource : CardLocalDataSource {
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

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        cards.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
