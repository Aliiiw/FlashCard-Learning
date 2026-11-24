package com.alirahimi.flashcard.feature.review.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.review.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.feature.review.domain.usecase.ReviewCardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: FakeReviewPresentationDataSource
    private lateinit var viewModel: ReviewViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeReviewPresentationDataSource()
        dataSource.cards.addAll(
            listOf(
                FlashCard(1L, "word1", "translation1", "EN", 1, 1000L, 500L, "Word"),
                FlashCard(2L, "word2", "translation2", "EN", 1, 1100L, 500L, "Word"),
                FlashCard(3L, "word3", "translation3", "EN", 1, 5000L, 500L, "Word")
            )
        )
        viewModel = ReviewViewModel(
            getCardsForReviewUseCase = GetCardsForReviewUseCase(dataSource),
            reviewCardUseCase = ReviewCardUseCase(dataSource),
            timeProvider = object : TimeProvider {
                override fun currentTimeEpochMs(): Long = 1200L
            }
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun reviewFlowAdvancesCardsWithoutFlashingNextTranslation() = runTest {
        assertThat(viewModel.state.value.totalCards).isEqualTo(2)
        assertThat(viewModel.state.value.currentCard?.front).isEqualTo("word1")
        assertThat(viewModel.state.value.isAnswerVisible).isFalse()

        viewModel.onAction(ReviewAction.OnRevealAnswerClick)
        assertThat(viewModel.state.value.isAnswerVisible).isTrue()
        assertThat(viewModel.state.value.displayedAnswer).isEqualTo("translation1")

        viewModel.onAction(ReviewAction.OnSubmitReview(isCorrect = true))

        assertThat(dataSource.cards.first { it.id == 1L }.box).isEqualTo(2)
        assertThat(viewModel.state.value.currentCard?.front).isEqualTo("word2")
        assertThat(viewModel.state.value.isAnswerVisible).isFalse()
        assertThat(viewModel.state.value.displayedAnswer).isEqualTo("translation1")

        viewModel.onAction(ReviewAction.OnRevealAnswerClick)
        assertThat(viewModel.state.value.displayedAnswer).isEqualTo("translation2")

        viewModel.onAction(ReviewAction.OnSubmitReview(isCorrect = false))
        assertThat(viewModel.state.value.isFinished).isTrue()
        assertThat(viewModel.state.value.currentCard).isNull()
    }

    @Test
    fun backClickEmitsNavigateBackEvent() = runTest {
        viewModel.events.test {
            viewModel.onAction(ReviewAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(ReviewEvent.NavigateBack)
        }
    }
}

private class FakeReviewPresentationDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.toList())

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs })

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> =
        cards.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        cards.add(card)
        return Result.Success(Unit)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> {
        val index = cards.indexOfFirst { it.id == id }
        if (index == -1) return Result.Error(DataError.Local.NOT_FOUND)
        cards[index] = cards[index].copy(box = box, nextReviewTimeEpochMs = nextReviewTimeEpochMs)
        return Result.Success(Unit)
    }

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        cards.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
