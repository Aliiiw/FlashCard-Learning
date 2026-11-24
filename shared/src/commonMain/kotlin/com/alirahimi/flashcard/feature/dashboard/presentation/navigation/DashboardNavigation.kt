package com.alirahimi.flashcard.feature.dashboard.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alirahimi.flashcard.feature.dashboard.presentation.DashboardRoot
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavGraphBuilder.dashboardGraph(
    onStartReview: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToCardList: () -> Unit,
    onNavigateToImport: () -> Unit
) {
    composable<DashboardRoute> {
        DashboardRoot(
            onStartReview = onStartReview,
            onNavigateToAddCard = onNavigateToAddCard,
            onNavigateToCardList = onNavigateToCardList,
            onNavigateToImport = onNavigateToImport
        )
    }
}
