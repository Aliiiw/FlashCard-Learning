package com.alirahimi.flashcard.feature.importcards.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alirahimi.flashcard.feature.importcards.presentation.ImportRoot
import kotlinx.serialization.Serializable

@Serializable
data object ImportRoute

fun NavGraphBuilder.importGraph(
    onNavigateBack: () -> Unit
) {
    composable<ImportRoute> {
        ImportRoot(onNavigateBack = onNavigateBack)
    }
}
