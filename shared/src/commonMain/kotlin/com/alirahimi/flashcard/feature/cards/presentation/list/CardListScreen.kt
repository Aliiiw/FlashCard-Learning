package com.alirahimi.flashcard.feature.cards.presentation.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirahimi.flashcard.core.designsystem.component.AppCard
import com.alirahimi.flashcard.core.designsystem.component.TagBadge
import com.alirahimi.flashcard.core.designsystem.component.TopBarHeader
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.presentation.ObserveAsEvents
import com.alirahimi.flashcard.core.presentation.clearFocusOnTap
import com.alirahimi.flashcard.core.presentation.resources.Res
import com.alirahimi.flashcard.core.presentation.resources.cd_delete_card
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CardListRoot(
    onNavigateBack: () -> Unit,
    viewModel: CardListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            CardListEvent.NavigateBack -> onNavigateBack()
            is CardListEvent.ShowError -> Unit
        }
    }

    CardListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun CardListScreen(
    state: CardListState,
    onAction: (CardListAction) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val deleteCardDescription = stringResource(Res.string.cd_delete_card)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .clearFocusOnTap()
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarHeader(
                title = "Word List",
                backLabel = "Back",
                onBackClick = { onAction(CardListAction.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(CardListAction.OnSearchQueryChange(it)) },
                label = { Text("Search word or translation", color = colorScheme.onSurfaceVariant) },
                textStyle = LocalTextStyle.current.copy(color = colorScheme.onBackground),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline,
                    cursorColor = colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lang:", color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                listOf("ALL", "EN", "FR").forEach { lang ->
                    val isSelected = state.selectedLanguageFilter == lang
                    InputChip(
                        selected = isSelected,
                        onClick = { onAction(CardListAction.OnLanguageFilterSelect(lang)) },
                        label = {
                            Text(
                                text = lang,
                                color = if (isSelected) Color.White else colorScheme.onBackground
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = colorScheme.primary,
                            containerColor = colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        border = null
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Box:", color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                InputChip(
                    selected = state.selectedBoxFilter == null,
                    onClick = { onAction(CardListAction.OnBoxFilterSelect(null)) },
                    label = {
                        Text(
                            text = "ALL",
                            color = if (state.selectedBoxFilter == null) Color.White else colorScheme.onBackground
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = colorScheme.secondary,
                        containerColor = colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    border = null
                )
                (1..5).forEach { boxNum ->
                    val isSelected = state.selectedBoxFilter == boxNum
                    InputChip(
                        selected = isSelected,
                        onClick = { onAction(CardListAction.OnBoxFilterSelect(boxNum)) },
                        label = {
                            Text(
                                text = "$boxNum",
                                color = if (isSelected) Color.White else colorScheme.onBackground
                            )
                        },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = colorScheme.secondary,
                            containerColor = colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        border = null
                    )
                }
            }

            state.error?.let { errorText ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorText.asString(),
                    color = colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = state.filteredCards,
                    key = { it.id }
                ) { card ->
                    CardListItem(
                        card = card,
                        deleteContentDescription = deleteCardDescription,
                        onDeleteClick = { onAction(CardListAction.OnDeleteCardClick(card.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardListItem(
    card: FlashCardUi,
    deleteContentDescription: String,
    onDeleteClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadiusDp = 12
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = card.front,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                    Text(
                        text = "(${card.language})",
                        fontSize = 12.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.back,
                    fontSize = 16.sp,
                    color = colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagBadge(
                        text = card.cardType,
                        containerColor = colorScheme.primary.copy(alpha = 0.15f),
                        textColor = colorScheme.primary
                    )
                    TagBadge(
                        text = card.boxLabel,
                        containerColor = colorScheme.secondary.copy(alpha = 0.15f),
                        textColor = colorScheme.secondary
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.semantics { contentDescription = deleteContentDescription }
            ) {
                Text("🗑️", fontSize = 20.sp)
            }
        }
    }
}

@Preview
@Composable
private fun CardListScreenPreview() {
    AppTheme {
        CardListScreen(
            state = CardListState(
                filteredCards = listOf(
                    FlashCardUi(1L, "attractive", "جذاب", "EN", "Box 1", 1, "Word"),
                    FlashCardUi(2L, "bonjour", "سلام", "FR", "Box 3", 3, "Word")
                )
            ),
            onAction = {}
        )
    }
}
