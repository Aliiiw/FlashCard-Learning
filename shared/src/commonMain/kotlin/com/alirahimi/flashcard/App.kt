package com.alirahimi.flashcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.designsystem.theme.ThemeController
import com.alirahimi.flashcard.feature.cards.presentation.navigation.AddCardRoute
import com.alirahimi.flashcard.feature.cards.presentation.navigation.CardListRoute
import com.alirahimi.flashcard.feature.cards.presentation.navigation.cardsGraph
import com.alirahimi.flashcard.feature.dashboard.presentation.navigation.DashboardRoute
import com.alirahimi.flashcard.feature.dashboard.presentation.navigation.dashboardGraph
import com.alirahimi.flashcard.feature.importcards.presentation.navigation.ImportRoute
import com.alirahimi.flashcard.feature.importcards.presentation.navigation.importGraph
import com.alirahimi.flashcard.feature.review.presentation.navigation.ReviewRoute
import com.alirahimi.flashcard.feature.review.presentation.navigation.reviewGraph
import org.koin.compose.koinInject

@Composable
fun App() {
    val themeController = koinInject<ThemeController>()
    val themeConfig by themeController.themeConfig.collectAsStateWithLifecycle()

    AppTheme(themeConfig = themeConfig) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = DashboardRoute
        ) {
            dashboardGraph(
                onStartReview = { navController.navigate(ReviewRoute) },
                onNavigateToAddCard = { navController.navigate(AddCardRoute) },
                onNavigateToCardList = { navController.navigate(CardListRoute) },
                onNavigateToImport = { navController.navigate(ImportRoute) }
            )
            cardsGraph(
                onNavigateBack = { navController.popBackStack() }
            )
            reviewGraph(
                onNavigateBack = { navController.popBackStack() }
            )
            importGraph(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
