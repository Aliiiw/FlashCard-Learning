package com.alirahimi.flashcard.presentation

import com.alirahimi.flashcard.domain.FakeCardRepository
import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.domain.usecase.GetAllCardsUseCase
import com.alirahimi.flashcard.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: FakeCardRepository
    private lateinit var addCardUseCase: AddCardUseCase
    private lateinit var reviewCardUseCase: ReviewCardUseCase
    private lateinit var getCardsForReviewUseCase: GetCardsForReviewUseCase
    private lateinit var getAllCardsUseCase: GetAllCardsUseCase

    private lateinit var addCardViewModel: AddCardViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var cardListViewModel: CardListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        repository = FakeCardRepository()
        addCardUseCase = AddCardUseCase(repository)
        reviewCardUseCase = ReviewCardUseCase(repository)
        getCardsForReviewUseCase = GetCardsForReviewUseCase(repository)
        getAllCardsUseCase = GetAllCardsUseCase(repository)

        addCardViewModel = AddCardViewModel(addCardUseCase)
        reviewViewModel = ReviewViewModel(getCardsForReviewUseCase, reviewCardUseCase)
        cardListViewModel = CardListViewModel(getAllCardsUseCase, repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAddCardViewModelValidationAndSave() {
        runBlocking {
            assertEquals("", addCardViewModel.word.value)
            assertEquals("", addCardViewModel.translation.value)
            assertNull(addCardViewModel.errorMessage.value)

            addCardViewModel.onWordChanged("  ")
            addCardViewModel.onTranslationChanged("سیب")
            addCardViewModel.saveCard(1000L)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            assertNotNull(addCardViewModel.errorMessage.value)
            assertFalse(addCardViewModel.isSaved.value)

            addCardViewModel.onWordChanged("apple")
            addCardViewModel.saveCard(1000L)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            assertTrue(addCardViewModel.isSaved.value)
            assertNull(addCardViewModel.errorMessage.value)

            val cards = repository.getAllCards()
            assertEquals(1, cards.size)
            assertEquals("apple", cards[0].front)
        }
    }

    @Test
    fun testReviewViewModelFlow() {
        runBlocking {
            val card1 = FlashCard(0L, "word1", "translation1", "EN", 1, 1000L, 500L, "Word")
            val card2 = FlashCard(0L, "word2", "translation2", "EN", 1, 1500L, 500L, "Word")
            repository.insertCard(card1)
            repository.insertCard(card2)

            reviewViewModel.loadCards(1200L)
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            assertEquals(1, reviewViewModel.cardsToReview.value.size)
            assertEquals("word1", reviewViewModel.currentCard.value?.front)
            assertFalse(reviewViewModel.isAnswerVisible.value)
            assertFalse(reviewViewModel.isFinished.value)

            reviewViewModel.revealAnswer()
            assertTrue(reviewViewModel.isAnswerVisible.value)

            reviewViewModel.submitReview(isCorrect = true, currentTimeEpochMs = 2000L)
            
            testDispatcher.scheduler.advanceUntilIdle()

            val updatedCard = repository.getAllCards().find { it.front == "word1" }!!
            assertEquals(2, updatedCard.box)

            assertTrue(reviewViewModel.isFinished.value)
            assertNull(reviewViewModel.currentCard.value)
        }
    }

    @Test
    fun testCardListViewModelFiltering() {
        runBlocking {
            val card1 = FlashCard(0L, "apple", "سیب", "EN", 1, 1000L, 500L, "Word")
            val card2 = FlashCard(0L, "bonjour", "سلام", "FR", 3, 1000L, 500L, "Word")
            repository.insertCard(card1)
            repository.insertCard(card2)

            val collectJob = launch(UnconfinedTestDispatcher(testDispatcher.scheduler)) {
                cardListViewModel.filteredCards.collect {}
            }

            cardListViewModel.loadCards()
            
            testDispatcher.scheduler.advanceUntilIdle()
            
            assertEquals(2, cardListViewModel.filteredCards.value.size)

            cardListViewModel.onSearchQueryChanged("app")
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, cardListViewModel.filteredCards.value.size)
            assertEquals("apple", cardListViewModel.filteredCards.value[0].front)

            cardListViewModel.onSearchQueryChanged("")
            cardListViewModel.onLanguageFilterChanged("FR")
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, cardListViewModel.filteredCards.value.size)
            assertEquals("bonjour", cardListViewModel.filteredCards.value[0].front)

            cardListViewModel.onLanguageFilterChanged("ALL")
            cardListViewModel.onBoxFilterChanged(1)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, cardListViewModel.filteredCards.value.size)
            assertEquals("apple", cardListViewModel.filteredCards.value[0].front)

            collectJob.cancel()
        }
    }
}
