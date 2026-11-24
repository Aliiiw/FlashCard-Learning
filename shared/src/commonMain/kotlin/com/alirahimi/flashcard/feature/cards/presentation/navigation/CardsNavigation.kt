package com.alirahimi.flashcard.feature.cards.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alirahimi.flashcard.feature.cards.presentation.addcard.AddCardRoot
import com.alirahimi.flashcard.feature.cards.presentation.list.CardListRoot
import kotlinx.serialization.Serializable

@Serializable
data object AddCardRoute

@Serializable
data object CardListRoute

fun NavGraphBuilder.cardsGraph(
    onNavigateBack: () -> Unit
) {
    composable<AddCardRoute> {
        AddCardRoot(onNavigateBack = onNavigateBack)
    }
    composable<CardListRoute> {
        CardListRoot(onNavigateBack = onNavigateBack)
    }
}
