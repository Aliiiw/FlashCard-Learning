package com.alirahimi.flashcard.feature.dashboard.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.alirahimi.flashcard.core.designsystem.theme.ThemeController
import com.alirahimi.flashcard.core.designsystem.theme.ThemeMode
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
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
class DashboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: FakeDashboardDataSource
    private lateinit var themeController: ThemeController
    private lateinit var viewModel: DashboardViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeDashboardDataSource()
        themeController = ThemeController()
        viewModel = DashboardViewModel(
            cardLocalDataSource = dataSource,
            timeProvider = object : TimeProvider {
                override fun currentTimeEpochMs(): Long = 1500L
            },
            themeController = themeController
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refreshComputesBoxCountsAndDueCardsCount() = runTest {
        dataSource.cards.addAll(
            listOf(
                FlashCard(1L, "a", "ب", "EN", 1, 1000L, 100L, "Word"),
                FlashCard(2L, "b", "پ", "EN", 1, 2000L, 100L, "Word"),
                FlashCard(3L, "c", "ت", "FR", 3, 1200L, 100L, "Word")
            )
        )

        viewModel.onAction(DashboardAction.OnRefresh)

        val state = viewModel.state.value
        assertThat(state.dueCardsCount).isEqualTo(2)
        assertThat(state.boxes.first { it.boxNumber == 1 }.count).isEqualTo(2)
        assertThat(state.boxes.first { it.boxNumber == 3 }.count).isEqualTo(1)
        assertThat(state.boxes.first { it.boxNumber == 2 }.count).isEqualTo(0)
    }

    @Test
    fun navigationActionsEmitExpectedEvents() = runTest {
        viewModel.events.test {
            viewModel.onAction(DashboardAction.OnStartReviewClick)
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToReview)

            viewModel.onAction(DashboardAction.OnAddCardClick)
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToAddCard)

            viewModel.onAction(DashboardAction.OnWordListClick)
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToCardList)

            viewModel.onAction(DashboardAction.OnImportClick)
            assertThat(awaitItem()).isEqualTo(DashboardEvent.NavigateToImport)
        }
    }

    @Test
    fun themeDialogAndModeSelectionUpdateState() = runTest {
        viewModel.onAction(DashboardAction.OnOpenThemeDialog)
        assertThat(viewModel.state.value.isThemeDialogVisible).isTrue()

        viewModel.onAction(DashboardAction.OnThemeModeSelect(ThemeMode.Light))
        assertThat(viewModel.state.value.themeMode).isEqualTo(ThemeMode.Light)

        viewModel.onAction(DashboardAction.OnDismissThemeDialog)
        assertThat(viewModel.state.value.isThemeDialogVisible).isFalse()
    }

    @Test
    fun refreshFailureSetsErrorStateAndEmitsErrorEvent() = runTest {
        dataSource.shouldReturnError = true

        viewModel.events.test {
            viewModel.onAction(DashboardAction.OnRefresh)
            assertThat(viewModel.state.value.error).isNotNull()
            val event = awaitItem()
            assertThat(event is DashboardEvent.ShowError).isTrue()
        }
    }
}

private class FakeDashboardDataSource : CardLocalDataSource {
    val cards = mutableListOf<FlashCard>()
    var shouldReturnError = false

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> =
        if (shouldReturnError) Result.Error(DataError.Local.UNKNOWN) else Result.Success(cards.toList())

    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> =
        if (shouldReturnError) Result.Error(DataError.Local.UNKNOWN)
        else Result.Success(cards.filter { it.nextReviewTimeEpochMs <= currentTimeEpochMs })

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> =
        Result.Error(DataError.Local.NOT_FOUND)

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> = Result.Success(Unit)

    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> =
        Result.Success(Unit)

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> = Result.Success(Unit)
}
