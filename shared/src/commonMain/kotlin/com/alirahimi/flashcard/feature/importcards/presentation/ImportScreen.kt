package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirahimi.flashcard.core.designsystem.component.SegmentedSelectionRow
import com.alirahimi.flashcard.core.designsystem.component.TopBarHeader
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.presentation.ObserveAsEvents
import com.alirahimi.flashcard.core.presentation.clearFocusOnTap
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportRoot(
    onNavigateBack: () -> Unit,
    viewModel: ImportViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val filePicker = rememberFilePicker { content ->
        viewModel.onAction(ImportAction.OnFileContentLoaded(content))
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ImportEvent.NavigateBack -> onNavigateBack()
            ImportEvent.LaunchFilePicker -> filePicker()
        }
    }

    ImportScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ImportScreen(
    state: ImportState,
    onAction: (ImportAction) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

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
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBarHeader(
                title = "Import Cards",
                backLabel = "Back",
                onBackClick = { onAction(ImportAction.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Target Language",
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )
            SegmentedSelectionRow(
                options = listOf("EN" to "English", "FR" to "French"),
                selectedKey = state.language,
                onSelect = { onAction(ImportAction.OnLanguageSelect(it)) },
                selectedContainerColor = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                onSelect = { onAction(ImportAction.OnCardTypeSelect(it)) },
                selectedContainerColor = colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            state.summary?.let { summary ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.secondary.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, colorScheme.secondary, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Import Completed!",
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.secondary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Imported: ${summary.importedCount} new cards.",
                            color = colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Skipped: ${summary.skippedCount} duplicates.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            state.error?.let { errorText ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.error.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, colorScheme.error, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Import Failed",
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.error,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorText.asString(),
                            color = colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Text(
                text = "Paste text (format: word -> translation)",
                color = colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = state.inputText,
                onValueChange = { onAction(ImportAction.OnInputTextChange(it)) },
                placeholder = {
                    Text(
                        text = "appearance -> ظاهر\nbeauty -> زیبایی\nugly -> زشت",
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.outline,
                    cursorColor = colorScheme.primary,
                    focusedTextColor = colorScheme.onBackground,
                    unfocusedTextColor = colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onAction(ImportAction.OnPickFileClick) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "📁 Pick .txt File",
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onAction(ImportAction.OnImportClick) },
                    enabled = state.inputText.isNotBlank() && !state.isImporting,
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    if (state.isImporting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Import",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            if (state.inputText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onAction(ImportAction.OnClearTextClick) }) {
                    Text(
                        text = "Clear Text",
                        color = colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ImportScreenPreview() {
    AppTheme {
        ImportScreen(
            state = ImportState(
                inputText = "appearance -> ظاهر\nbeauty -> زیبایی",
                summary = ImportSummaryUi(importedCount = 2, skippedCount = 0)
            ),
            onAction = {}
        )
    }
}
