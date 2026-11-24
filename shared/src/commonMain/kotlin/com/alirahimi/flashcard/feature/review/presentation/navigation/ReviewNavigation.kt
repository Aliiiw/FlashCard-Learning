package com.alirahimi.flashcard.feature.review.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alirahimi.flashcard.feature.review.presentation.ReviewRoot
import kotlinx.serialization.Serializable

@Serializable
data object ReviewRoute

fun NavGraphBuilder.reviewGraph(
    onNavigateBack: () -> Unit
) {
    composable<ReviewRoute> {
        ReviewRoot(onNavigateBack = onNavigateBack)
    }
}
