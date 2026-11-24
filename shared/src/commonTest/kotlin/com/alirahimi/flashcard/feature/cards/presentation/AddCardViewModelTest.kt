package com.alirahimi.flashcard.feature.cards.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.cards.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.feature.cards.presentation.addcard.AddCardAction
import com.alirahimi.flashcard.feature.cards.presentation.addcard.AddCardEvent
import com.alirahimi.flashcard.feature.cards.presentation.addcard.AddCardViewModel
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
class AddCardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: FakeCardsPresentationDataSource
    private lateinit var viewModel: AddCardViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeCardsPresentationDataSource()
        viewModel = AddCardViewModel(
            savedStateHandle = SavedStateHandle(),
            addCardUseCase = AddCardUseCase(dataSource),
            timeProvider = object : TimeProvider {
                override fun currentTimeEpochMs(): Long = 1000L
            }
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun restoresFieldsFromSavedStateHandle() {
        val restored = AddCardViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "add_card_word" to "bonjour",
                    "add_card_translation" to "سلام",
                    "add_card_language" to "FR",
                    "add_card_card_type" to "Phrase"
                )
            ),
            addCardUseCase = AddCardUseCase(dataSource),
            timeProvider = object : TimeProvider {
                override fun currentTimeEpochMs(): Long = 1000L
            }
        )

        assertThat(restored.state.value.word).isEqualTo("bonjour")
        assertThat(restored.state.value.translation).isEqualTo("سلام")
        assertThat(restored.state.value.language).isEqualTo("FR")
        assertThat(restored.state.value.cardType).isEqualTo("Phrase")
    }

    @Test
    fun validationErrorIsShownAndClearedOnInput() = runTest {
        viewModel.onAction(AddCardAction.OnWordChange("  "))
        viewModel.onAction(AddCardAction.OnTranslationChange("سیب"))
        viewModel.onAction(AddCardAction.OnSaveClick)

        assertThat(viewModel.state.value.error).isNotNull()
        assertThat(dataSource.cards).hasSize(0)

        viewModel.onAction(AddCardAction.OnWordChange("apple"))
        assertThat(viewModel.state.value.error).isNull()
    }

    @Test
    fun savingValidCardClearsInputsAndEmitsCardSavedEvent() = runTest {
        viewModel.events.test {
            viewModel.onAction(AddCardAction.OnWordChange("apple"))
            viewModel.onAction(AddCardAction.OnTranslationChange("سیب"))
            viewModel.onAction(AddCardAction.OnSaveClick)

            assertThat(awaitItem()).isEqualTo(AddCardEvent.CardSaved)
            assertThat(viewModel.state.value.word).isEqualTo("")
            assertThat(viewModel.state.value.translation).isEqualTo("")
            assertThat(viewModel.state.value.error).isNull()
            assertThat(dataSource.cards).hasSize(1)
            assertThat(dataSource.cards.first().front).isEqualTo("apple")
        }
    }

    @Test
    fun backClickEmitsNavigateBackEvent() = runTest {
        viewModel.events.test {
            viewModel.onAction(AddCardAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(AddCardEvent.NavigateBack)
        }
    }
}

internal class FakeCardsPresentationDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()
    var shouldReturnError = false
    private var nextId = 1L

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> =
        if (shouldReturnError) Result.Error(DataError.Local.UNKNOWN) else Result.Success(cards.toList())

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs })

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> =
        cards.find { it.id == id }?.let { Result.Success(it) }
            ?: Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.DISK_FULL)
        cards.add(card.copy(id = nextId++))
        return Result.Success(Unit)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        if (shouldReturnError) return Result.Error(DataError.Local.UNKNOWN)
        cards.removeAll { it.id == id }
        return Result.Success(Unit)
    }
}
