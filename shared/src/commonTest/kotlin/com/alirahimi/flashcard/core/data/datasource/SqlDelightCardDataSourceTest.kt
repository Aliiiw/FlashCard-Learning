package com.alirahimi.flashcard.core.data.datasource

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.alirahimi.flashcard.core.data.createTestSqlDriver
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.db.FlashCardDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SqlDelightCardDataSourceTest {

    private lateinit var database: FlashCardDatabase
    private lateinit var dataSource: SqlDelightCardDataSource

    @BeforeTest
    fun setUp() {
        val driver = createTestSqlDriver()
        database = FlashCardDatabase(driver)
        database.flashCardDatabaseQueries.clearDatabase()
        dataSource = SqlDelightCardDataSource(
            database = database,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    @Test
    fun crudOperationsWorkAsExpected() = runTest {
        val card = FlashCard(
            id = 0L,
            front = "hello",
            back = "سلام",
            language = "EN",
            box = 1,
            nextReviewTimeEpochMs = 1000L,
            createdAtEpochMs = 500L,
            cardType = "Word"
        )

        assertThat(dataSource.insertCard(card)).isInstanceOf<Result.Success<Unit>>()

        val allResult = dataSource.getAllCards()
        assertThat(allResult).isInstanceOf<Result.Success<List<FlashCard>>>()
        val savedCards = (allResult as Result.Success).data
        assertThat(savedCards).hasSize(1)
        val savedCard = savedCards.first()
        assertThat(savedCard.front).isEqualTo("hello")

        val byIdResult = dataSource.getCardById(savedCard.id)
        assertThat(byIdResult).isInstanceOf<Result.Success<FlashCard>>()
        assertThat((byIdResult as Result.Success).data.back).isEqualTo("سلام")

        dataSource.updateCardReview(savedCard.id, box = 2, nextReviewTimeEpochMs = 2000L)
        val updated = (dataSource.getCardById(savedCard.id) as Result.Success).data
        assertThat(updated.box).isEqualTo(2)
        assertThat(updated.nextReviewTimeEpochMs).isEqualTo(2000L)

        val notDue = (dataSource.getCardsForReview(1500L) as Result.Success).data
        assertThat(notDue).hasSize(0)

        val due = (dataSource.getCardsForReview(2500L) as Result.Success).data
        assertThat(due).hasSize(1)

        dataSource.deleteCard(savedCard.id)
        val missingResult = dataSource.getCardById(savedCard.id)
        assertThat(missingResult).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }
}
