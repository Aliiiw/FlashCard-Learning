package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alirahimi.flashcard.presentation.CardListViewModel

import com.alirahimi.flashcard.ui.LocalAppColors

@Composable
fun CardListScreen(
    viewModel: CardListViewModel,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLanguage by viewModel.selectedLanguageFilter.collectAsState()
    val selectedBox by viewModel.selectedBoxFilter.collectAsState()
    val cards by viewModel.filteredCards.collectAsState()

    val colors = LocalAppColors.current

    LaunchedEffect(Unit) {
        viewModel.loadCards()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize()
                .padding(20.dp),
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
                    text = "Word List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search word or translation", color = colors.textSecondary) },
                textStyle = LocalTextStyle.current.copy(color = colors.textPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderColor,
                    cursorColor = colors.primary
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
                Text("Lang:", color = colors.textSecondary, fontSize = 14.sp)
                listOf("ALL", "EN", "FR").forEach { lang ->
                    InputChip(
                        selected = selectedLanguage == lang,
                        onClick = { viewModel.onLanguageFilterChanged(lang) },
                        label = { Text(lang, color = if (selectedLanguage == lang) Color.White else colors.textPrimary) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = colors.primary,
                            containerColor = colors.borderColor.copy(alpha = 0.2f)
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
                Text("Box:", color = colors.textSecondary, fontSize = 14.sp)
                InputChip(
                    selected = selectedBox == null,
                    onClick = { viewModel.onBoxFilterChanged(null) },
                    label = { Text("ALL", color = if (selectedBox == null) Color.White else colors.textPrimary) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = colors.accent,
                        containerColor = colors.borderColor.copy(alpha = 0.2f)
                    ),
                    border = null
                )
                (1..5).forEach { boxNum ->
                    InputChip(
                        selected = selectedBox == boxNum,
                        onClick = { viewModel.onBoxFilterChanged(boxNum) },
                        label = { Text("$boxNum", color = if (selectedBox == boxNum) Color.White else colors.textPrimary) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = colors.accent,
                            containerColor = colors.borderColor.copy(alpha = 0.2f)
                        ),
                        border = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cards, key = { it.id }) { card ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
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
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "(${card.language})",
                                        fontSize = 12.sp,
                                        color = colors.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = card.back,
                                    fontSize = 16.sp,
                                    color = colors.accent
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    BoxBadge(
                                        text = card.cardType,
                                        containerColor = colors.primary.copy(alpha = 0.15f),
                                        textColor = colors.primary
                                    )
                                    BoxBadge(
                                        text = "Box ${card.box}",
                                        containerColor = colors.accent.copy(alpha = 0.15f),
                                        textColor = colors.accent
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deleteCard(card.id) }
                            ) {
                                Text("🗑️", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoxBadge(text: String, containerColor: Color, textColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
