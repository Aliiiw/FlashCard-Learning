package com.alirahimi.flashcard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.alirahimi.flashcard.presentation.AddCardViewModel
import com.alirahimi.flashcard.presentation.CardListViewModel
import com.alirahimi.flashcard.presentation.ReviewViewModel
import com.alirahimi.flashcard.ui.AddCardScreen
import com.alirahimi.flashcard.ui.CardListScreen
import com.alirahimi.flashcard.ui.DashboardScreen
import com.alirahimi.flashcard.ui.ReviewScreen
import org.koin.compose.koinInject

enum class Screen {
    Dashboard,
    AddCard,
    Review,
    CardList
}

@Composable
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

        val addCardViewModel = koinInject<AddCardViewModel>()
        val reviewViewModel = koinInject<ReviewViewModel>()
        val cardListViewModel = koinInject<CardListViewModel>()

        val cards by cardListViewModel.cards.collectAsState()
        val dueCards by reviewViewModel.cardsToReview.collectAsState()

        LaunchedEffect(currentScreen) {
            if (currentScreen == Screen.Dashboard) {
                cardListViewModel.loadCards()
                reviewViewModel.loadCards(getCurrentTimeEpochMs())
            }
        }

        when (currentScreen) {
            Screen.Dashboard -> {
                DashboardScreen(
                    cards = cards,
                    dueCardsCount = dueCards.size,
                    onStartReview = { currentScreen = Screen.Review },
                    onNavigateToAddCard = { currentScreen = Screen.AddCard },
                    onNavigateToCardList = { currentScreen = Screen.CardList }
                )
            }
            Screen.AddCard -> {
                AddCardScreen(
                    viewModel = addCardViewModel,
                    onBack = { currentScreen = Screen.Dashboard }
                )
            }
            Screen.Review -> {
                ReviewScreen(
                    viewModel = reviewViewModel,
                    onBack = { currentScreen = Screen.Dashboard }
                )
            }
            Screen.CardList -> {
                CardListScreen(
                    viewModel = cardListViewModel,
                    onBack = { currentScreen = Screen.Dashboard }
                )
            }
        }
    }
}