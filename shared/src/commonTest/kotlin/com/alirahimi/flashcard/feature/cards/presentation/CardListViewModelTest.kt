package com.alirahimi.flashcard.feature.cards.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.cards.domain.usecase.DeleteCardUseCase
import com.alirahimi.flashcard.feature.cards.domain.usecase.GetAllCardsUseCase
import com.alirahimi.flashcard.feature.cards.presentation.list.CardListAction
import com.alirahimi.flashcard.feature.cards.presentation.list.CardListEvent
import com.alirahimi.flashcard.feature.cards.presentation.list.CardListViewModel
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
class CardListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: FakeCardsPresentationDataSource
    private lateinit var viewModel: CardListViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        dataSource = FakeCardsPresentationDataSource()
        dataSource.cards.addAll(
            listOf(
                FlashCard(1L, "apple", "سیب", "EN", 1, 1000L, 500L, "Word"),
                FlashCard(2L, "bonjour", "سلام", "FR", 3, 1000L, 500L, "Word")
            )
        )
        viewModel = CardListViewModel(
            getAllCardsUseCase = GetAllCardsUseCase(dataSource),
            deleteCardUseCase = DeleteCardUseCase(dataSource)
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun filtersBySearchLanguageAndBox() = runTest {
        assertThat(viewModel.state.value.filteredCards).hasSize(2)

        viewModel.onAction(CardListAction.OnSearchQueryChange("app"))
        assertThat(viewModel.state.value.filteredCards).hasSize(1)
        assertThat(viewModel.state.value.filteredCards.first().front).isEqualTo("apple")

        viewModel.onAction(CardListAction.OnSearchQueryChange(""))
        viewModel.onAction(CardListAction.OnLanguageFilterSelect("FR"))
        assertThat(viewModel.state.value.filteredCards).hasSize(1)
        assertThat(viewModel.state.value.filteredCards.first().front).isEqualTo("bonjour")

        viewModel.onAction(CardListAction.OnLanguageFilterSelect("ALL"))
        viewModel.onAction(CardListAction.OnBoxFilterSelect(1))
        assertThat(viewModel.state.value.filteredCards).hasSize(1)
        assertThat(viewModel.state.value.filteredCards.first().front).isEqualTo("apple")
    }

    @Test
    fun deleteCardRemovesItemFromState() = runTest {
        viewModel.onAction(CardListAction.OnDeleteCardClick(1L))
        assertThat(viewModel.state.value.filteredCards).hasSize(1)
        assertThat(viewModel.state.value.filteredCards.first().id).isEqualTo(2L)
    }

    @Test
    fun backClickEmitsNavigateBack() = runTest {
        viewModel.events.test {
            viewModel.onAction(CardListAction.OnBackClick)
            assertThat(awaitItem()).isEqualTo(CardListEvent.NavigateBack)
        }
    }
}
