package com.alirahimi.flashcard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.alirahimi.flashcard.presentation.AddCardViewModel
import com.alirahimi.flashcard.presentation.CardListViewModel
import com.alirahimi.flashcard.presentation.ReviewViewModel
import com.alirahimi.flashcard.presentation.ImportViewModel
import com.alirahimi.flashcard.ui.AddCardScreen
import com.alirahimi.flashcard.ui.CardListScreen
import com.alirahimi.flashcard.ui.DashboardScreen
import com.alirahimi.flashcard.ui.ReviewScreen
import com.alirahimi.flashcard.ui.ImportScreen
import org.koin.compose.koinInject

import com.alirahimi.flashcard.ui.*

enum class Screen {
    Dashboard,
    AddCard,
    Review,
    CardList,
    Import
}

@Composable
fun App() {
    var currentThemeMode by remember { mutableStateOf(ThemeMode.Dark) }
    var selectedCustomPresetColor by remember { mutableStateOf(CustomThemePresets[0].color) }

    val appColors = when (currentThemeMode) {
        ThemeMode.Dark -> DarkColors
        ThemeMode.Light -> LightColors
        ThemeMode.Custom -> getCustomColors(selectedCustomPresetColor)
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme {
            var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

            val addCardViewModel = koinInject<AddCardViewModel>()
            val reviewViewModel = koinInject<ReviewViewModel>()
            val cardListViewModel = koinInject<CardListViewModel>()
            val importViewModel = koinInject<ImportViewModel>()

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
                        themeMode = currentThemeMode,
                        customPresetColor = selectedCustomPresetColor,
                        onThemeModeChanged = { currentThemeMode = it },
                        onCustomColorChanged = { selectedCustomPresetColor = it },
                        onStartReview = { currentScreen = Screen.Review },
                        onNavigateToAddCard = { currentScreen = Screen.AddCard },
                        onNavigateToCardList = { currentScreen = Screen.CardList },
                        onNavigateToImport = { currentScreen = Screen.Import }
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
                Screen.Import -> {
                    ImportScreen(
                        viewModel = importViewModel,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }
}