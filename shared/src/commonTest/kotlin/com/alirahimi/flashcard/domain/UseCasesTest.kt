package com.alirahimi.flashcard.domain

import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.domain.usecase.GetAllCardsUseCase
import com.alirahimi.flashcard.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UseCasesTest {
    private lateinit var repository: FakeCardRepository
    private lateinit var addCardUseCase: AddCardUseCase
    private lateinit var reviewCardUseCase: ReviewCardUseCase
    private lateinit var getCardsForReviewUseCase: GetCardsForReviewUseCase
    private lateinit var getAllCardsUseCase: GetAllCardsUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeCardRepository()
        addCardUseCase = AddCardUseCase(repository)
        reviewCardUseCase = ReviewCardUseCase(repository)
        getCardsForReviewUseCase = GetCardsForReviewUseCase(repository)
        getAllCardsUseCase = GetAllCardsUseCase(repository)
    }

    @Test
    fun testAddCardSuccess() {
        runBlocking {
            addCardUseCase(
                front = "apple",
                back = "سیب",
                language = "EN",
                cardType = "Word",
                currentTimeEpochMs = 1000L
            )
            val cards = repository.getAllCards()
            assertEquals(1, cards.size)
            val card = cards[0]
            assertEquals("apple", card.front)
            assertEquals("سیب", card.back)
            assertEquals("EN", card.language)
            assertEquals(1, card.box)
            assertEquals(1000L, card.createdAtEpochMs)
            assertEquals(1000L, card.nextReviewTimeEpochMs)
        }
    }

    @Test
    fun testAddCardValidationFails() {
        runBlocking {
            assertFailsWith<IllegalArgumentException> {
                addCardUseCase(
                    front = "",
                    back = "سیب",
                    language = "EN",
                    cardType = "Word",
                    currentTimeEpochMs = 1000L
                )
            }
            assertFailsWith<IllegalArgumentException> {
                addCardUseCase(
                    front = "apple",
                    back = "",
                    language = "EN",
                    cardType = "Word",
                    currentTimeEpochMs = 1000L
                )
            }
        }
    }

    @Test
    fun testReviewCardCorrectUpgradesBox() {
        runBlocking {
            val initialCard = FlashCard(
                id = 0L,
                front = "apple",
                back = "سیب",
                language = "EN",
                box = 1,
                nextReviewTimeEpochMs = 1000L,
                createdAtEpochMs = 1000L,
                cardType = "Word"
            )
            repository.insertCard(initialCard)
            val insertedCard = repository.getAllCards()[0]

            reviewCardUseCase(insertedCard.id, isCorrect = true, currentTimeEpochMs = 2000L)

            val updatedCard = repository.getCardById(insertedCard.id)!!
            assertEquals(2, updatedCard.box)
            val expectedReviewTime = 2000L + (2L * 24 * 60 * 60 * 1000)
            assertEquals(expectedReviewTime, updatedCard.nextReviewTimeEpochMs)
        }
    }

    @Test
    fun testReviewCardIncorrectResetsToBox1() {
        runBlocking {
            val initialCard = FlashCard(
                id = 0L,
                front = "apple",
                back = "سیب",
                language = "EN",
                box = 4,
                nextReviewTimeEpochMs = 1000L,
                createdAtEpochMs = 1000L,
                cardType = "Word"
            )
            repository.insertCard(initialCard)
            val insertedCard = repository.getAllCards()[0]

            reviewCardUseCase(insertedCard.id, isCorrect = false, currentTimeEpochMs = 2000L)

            val updatedCard = repository.getCardById(insertedCard.id)!!
            assertEquals(1, updatedCard.box)
            val expectedReviewTime = 2000L + (1L * 24 * 60 * 60 * 1000)
            assertEquals(expectedReviewTime, updatedCard.nextReviewTimeEpochMs)
        }
    }

    @Test
    fun testGetCardsForReviewFiltersCorrectly() {
        runBlocking {
            val card1 = FlashCard(0L, "c1", "b1", "EN", 1, 1000L, 500L, "Word")
            val card2 = FlashCard(0L, "c2", "b2", "EN", 1, 3000L, 500L, "Word")
            repository.insertCard(card1)
            repository.insertCard(card2)

            val reviewCards = getCardsForReviewUseCase(2000L)
            assertEquals(1, reviewCards.size)
            assertEquals("c1", reviewCards[0].front)
        }
    }
}
