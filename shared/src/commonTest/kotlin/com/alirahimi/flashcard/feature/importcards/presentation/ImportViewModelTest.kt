package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.importcards.domain.usecase.ImportCardsUseCase
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
class ImportViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: FakeImportPresentationDataSource
    private lateinit var viewModel: ImportViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeImportPresentationDataSource()
        viewModel = ImportViewModel(
            savedStateHandle = SavedStateHandle(),
            importCardsUseCase = ImportCardsUseCase(dataSource),
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
    fun restoresSavedInputFromSavedStateHandle() {
        val restored = ImportViewModel(
            savedStateHandle = SavedStateHandle(mapOf("import_input_text" to "tall -> بلند")),
            importCardsUseCase = ImportCardsUseCase(dataSource),
            timeProvider = object : TimeProvider {
                override fun currentTimeEpochMs(): Long = 1000L
            }
        )
        assertThat(restored.state.value.inputText).isEqualTo("tall -> بلند")
    }

    @Test
    fun pickFileClickEmitsLaunchFilePickerEvent() = runTest {
        viewModel.events.test {
            viewModel.onAction(ImportAction.OnPickFileClick)
            assertThat(awaitItem()).isEqualTo(ImportEvent.LaunchFilePicker)
        }
    }

    @Test
    fun importingTextProducesSummaryAndHandlesClear() = runTest {
        viewModel.onAction(ImportAction.OnFileContentLoaded("beauty -> زیبایی\nugly -> زشت"))
        viewModel.onAction(ImportAction.OnImportClick)

        assertThat(viewModel.state.value.summary).isEqualTo(ImportSummaryUi(2, 0))
        assertThat(viewModel.state.value.error).isNull()

        viewModel.onAction(ImportAction.OnClearTextClick)
        assertThat(viewModel.state.value.inputText).isEqualTo("")
        assertThat(viewModel.state.value.summary).isNull()
    }

    @Test
    fun importingInvalidTextShowsValidationError() = runTest {
        viewModel.onAction(ImportAction.OnInputTextChange("invalid line"))
        viewModel.onAction(ImportAction.OnImportClick)

        assertThat(viewModel.state.value.summary).isNull()
        assertThat(viewModel.state.value.error).isNotNull()
    }
}

private class FakeImportPresentationDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()
    private var nextId = 1L

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards.toList())

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> =
        Result.Success(cards)

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> =
        Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        cards.add(card.copy(id = nextId++))
        return Result.Success(Unit)
    }

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> = Result.Success(Unit)
}
