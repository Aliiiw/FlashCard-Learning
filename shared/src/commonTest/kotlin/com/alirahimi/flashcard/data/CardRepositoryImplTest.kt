package com.alirahimi.flashcard.data

import com.alirahimi.flashcard.data.repository.CardRepositoryImpl
import com.alirahimi.flashcard.db.FlashCardDatabase
import com.alirahimi.flashcard.domain.model.FlashCard
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CardRepositoryImplTest {
    private lateinit var database: FlashCardDatabase
    private lateinit var repository: CardRepositoryImpl

    @BeforeTest
    fun setUp() {
        val driver = createTestSqlDriver()
        database = FlashCardDatabase(driver)
        repository = CardRepositoryImpl(database)
    }

    @Test
    fun testRepositoryCrud() {
        runBlocking {
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
            repository.insertCard(card)

            val allCards = repository.getAllCards()
            assertEquals(1, allCards.size)
            val savedCard = allCards[0]
            assertEquals("hello", savedCard.front)

            val retrievedCard = repository.getCardById(savedCard.id)
            assertNotNull(retrievedCard)
            assertEquals("hello", retrievedCard.front)

            repository.updateCardReview(savedCard.id, box = 2, nextReviewTimeEpochMs = 2000L)
            val updatedCard = repository.getCardById(savedCard.id)!!
            assertEquals(2, updatedCard.box)
            assertEquals(2000L, updatedCard.nextReviewTimeEpochMs)

            val reviewCards = repository.getCardsForReview(1500L)
            assertEquals(0, reviewCards.size)

            val reviewCardsDue = repository.getCardsForReview(2500L)
            assertEquals(1, reviewCardsDue.size)

            repository.deleteCard(savedCard.id)
            assertNull(repository.getCardById(savedCard.id))
        }
    }
}
