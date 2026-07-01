package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alirahimi.flashcard.getCurrentTimeEpochMs
import com.alirahimi.flashcard.presentation.ImportResult
import com.alirahimi.flashcard.presentation.ImportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    viewModel: ImportViewModel,
    onBack: () -> Unit
) {
    val inputText by viewModel.inputText.collectAsState()
    val language by viewModel.language.collectAsState()
    val cardType by viewModel.cardType.collectAsState()
    val isImporting by viewModel.isImporting.collectAsState()
    val importResult by viewModel.importResult.collectAsState()

    val colors = LocalAppColors.current
    val scrollState = rememberScrollState()

    val filePicker = rememberFilePicker { content ->
        viewModel.onInputTextChanged(content)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBack) {
                    Text("Back", color = colors.textSecondary, fontSize = 16.sp)
                }
                Text(
                    text = "Import Cards",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Target Language",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onLanguageChanged("EN") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (language == "EN") colors.primary else colors.borderColor.copy(
                            alpha = 0.2f
                        ),
                        contentColor = if (language == "EN") Color.White else colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("English")
                }
                Button(
                    onClick = { viewModel.onLanguageChanged("FR") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (language == "FR") colors.primary else colors.borderColor.copy(
                            alpha = 0.2f
                        ),
                        contentColor = if (language == "FR") Color.White else colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("French")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Card Type",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onCardTypeChanged("Word") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cardType == "Word") colors.primary else colors.borderColor.copy(
                            alpha = 0.2f
                        ),
                        contentColor = if (cardType == "Word") Color.White else colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Word")
                }
                Button(
                    onClick = { viewModel.onCardTypeChanged("Phrase / Idiom") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cardType == "Phrase / Idiom") colors.primary else colors.borderColor.copy(
                            alpha = 0.2f
                        ),
                        contentColor = if (cardType == "Phrase / Idiom") Color.White else colors.textPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Phrase / Idiom")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (val result = importResult) {
                is ImportResult.Success -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF10B981).copy(
                                alpha = 0.15f
                            )
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Import Completed!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Imported: ${result.importedCount} new cards.",
                                color = colors.textPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                "Skipped: ${result.skippedCount} duplicates.",
                                color = colors.textSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                is ImportResult.Error -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFEF4444).copy(
                                alpha = 0.15f
                            )
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Import Failed",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444),
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                result.message,
                                color = colors.textPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                ImportResult.Idle -> {}
            }

            Text(
                text = "Paste text (format: word -> translation)",
                color = colors.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = inputText,
                onValueChange = viewModel::onInputTextChanged,
                placeholder = {
                    Text(
                        "appearance -> ظاهر\nbeauty -> زیبایی\nugly -> زشت",
                        color = colors.textSecondary.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderColor,
                    cursorColor = colors.primary,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
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
                    onClick = filePicker,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(
                        "📁 Pick .txt File",
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.importWords(inputText, getCurrentTimeEpochMs())
                    },
                    enabled = inputText.isNotBlank() && !isImporting,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    if (isImporting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Import", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            if (inputText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = viewModel::clearInputText) {
                    Text("Clear Text", color = colors.primary, fontSize = 14.sp)
                }
            }
        }
    }
}
