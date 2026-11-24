package com.alirahimi.flashcard.feature.review.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirahimi.flashcard.core.designsystem.component.AppCard
import com.alirahimi.flashcard.core.designsystem.component.PrimaryButton
import com.alirahimi.flashcard.core.designsystem.component.TopBarHeader
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.presentation.ObserveAsEvents
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReviewRoot(
    onNavigateBack: () -> Unit,
    viewModel: ReviewViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ReviewEvent.NavigateBack -> onNavigateBack()
            is ReviewEvent.ShowError -> Unit
        }
    }

    ReviewScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ReviewScreen(
    state: ReviewState,
    onAction: (ReviewAction) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val answerAlpha by animateFloatAsState(
        targetValue = if (state.isAnswerVisible) 1f else 0f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarHeader(
                title = "Review",
                backLabel = "Quit",
                onBackClick = { onAction(ReviewAction.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            state.error?.let { errorText ->
                Text(
                    text = errorText.asString(),
                    color = colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            val card = state.currentCard
            if (state.isFinished || card == null) {
                ReviewCompletedContent(
                    onBackToDashboard = { onAction(ReviewAction.OnBackClick) }
                )
            } else {
                LinearProgressIndicator(
                    progress = { state.progress },
                    color = colorScheme.primary,
                    trackColor = colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = state.progressLabel,
                    fontSize = 14.sp,
                    color = colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 10.dp),
                    cornerRadiusDp = 24
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(card.language, color = Color.White) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = colorScheme.primary
                                )
                            )
                            SuggestionChip(
                                onClick = {},
                                label = { Text(card.cardType, color = Color.White) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = colorScheme.secondary
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = card.boxLabel,
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = card.front,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = state.displayedAnswer,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.secondary,
                                modifier = Modifier.graphicsLayer { alpha = answerAlpha }
                            )
                        }

                        if (!state.isAnswerVisible) {
                            PrimaryButton(
                                text = "Reveal Answer",
                                onClick = { onAction(ReviewAction.OnRevealAnswerClick) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                PrimaryButton(
                                    text = "Incorrect",
                                    onClick = { onAction(ReviewAction.OnSubmitReview(isCorrect = false)) },
                                    containerColor = colorScheme.error,
                                    modifier = Modifier.weight(1f)
                                )
                                PrimaryButton(
                                    text = "Correct",
                                    onClick = { onAction(ReviewAction.OnSubmitReview(isCorrect = true)) },
                                    containerColor = colorScheme.secondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCompletedContent(
    onBackToDashboard: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            cornerRadiusDp = 24
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎉", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "All Caught Up!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have reviewed all due cards for now.",
                    fontSize = 16.sp,
                    color = colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                PrimaryButton(
                    text = "Back to Dashboard",
                    onClick = onBackToDashboard,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun ReviewScreenPreview() {
    AppTheme {
        ReviewScreen(
            state = ReviewState(
                currentCard = ReviewCardUi(
                    id = 1L,
                    front = "gorgeous",
                    back = "بسیار زیبا",
                    language = "EN",
                    cardType = "Word",
                    boxLabel = "Box 2"
                ),
                displayedAnswer = "بسیار زیبا",
                currentIndex = 1,
                totalCards = 5,
                isAnswerVisible = true
            ),
            onAction = {}
        )
    }
}
