package com.alirahimi.flashcard.feature.cards.presentation.addcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirahimi.flashcard.core.designsystem.component.LanguageOutlinedTextField
import com.alirahimi.flashcard.core.designsystem.component.PrimaryButton
import com.alirahimi.flashcard.core.designsystem.component.SegmentedSelectionRow
import com.alirahimi.flashcard.core.designsystem.component.TopBarHeader
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.presentation.ObserveAsEvents
import com.alirahimi.flashcard.core.presentation.clearFocusOnTap
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddCardRoot(
    onNavigateBack: () -> Unit,
    viewModel: AddCardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddCardEvent.NavigateBack -> onNavigateBack()
            AddCardEvent.CardSaved -> Unit
        }
    }

    AddCardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun AddCardScreen(
    state: AddCardState,
    onAction: (AddCardAction) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val wordLanguageCode = if (state.language == "EN") "en" else "fr"

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
                title = "Add Card",
                backLabel = "Back",
                onBackClick = { onAction(AddCardAction.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Language",
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            SegmentedSelectionRow(
                options = listOf("EN" to "English", "FR" to "French"),
                selectedKey = state.language,
                onSelect = { onAction(AddCardAction.OnLanguageSelect(it)) },
                selectedContainerColor = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Card Type",
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            SegmentedSelectionRow(
                options = listOf("Word" to "Word", "Phrase" to "Phrase / Idiom"),
                selectedKey = state.cardType,
                onSelect = { onAction(AddCardAction.OnCardTypeSelect(it)) },
                selectedContainerColor = colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            LanguageOutlinedTextField(
                value = state.word,
                onValueChange = { onAction(AddCardAction.OnWordChange(it)) },
                label = "Word / Phrase",
                languageCode = wordLanguageCode,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LanguageOutlinedTextField(
                value = state.translation,
                onValueChange = { onAction(AddCardAction.OnTranslationChange(it)) },
                label = "Farsi Translation",
                languageCode = "fa",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            state.error?.let { errorText ->
                Text(
                    text = errorText.asString(),
                    color = colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            PrimaryButton(
                text = "Save Card",
                onClick = { onAction(AddCardAction.OnSaveClick) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun AddCardScreenPreview() {
    AppTheme {
        AddCardScreen(
            state = AddCardState(
                word = "resilient",
                translation = "انعطاف‌پذیر",
                language = "EN",
                cardType = "Word"
            ),
            onAction = {}
        )
    }
}
