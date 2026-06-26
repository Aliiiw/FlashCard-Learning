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

@Composable
fun CardListScreen(
    viewModel: CardListViewModel,
    onBack: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLanguage by viewModel.selectedLanguageFilter.collectAsState()
    val selectedBox by viewModel.selectedBoxFilter.collectAsState()
    val cards by viewModel.filteredCards.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCards()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F111A))
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
                    Text("Back", color = Color(0xFFA5B4FC), fontSize = 16.sp)
                }
                Text(
                    text = "Word List",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search word or translation", color = Color(0xFFA5B4FC)) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedBorderColor = Color(0xFF2C3258),
                    cursorColor = Color(0xFF7C4DFF)
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
                Text("Lang:", color = Color(0xFFA5B4FC), fontSize = 14.sp)
                listOf("ALL", "EN", "FR").forEach { lang ->
                    InputChip(
                        selected = selectedLanguage == lang,
                        onClick = { viewModel.onLanguageFilterChanged(lang) },
                        label = { Text(lang, color = Color.White) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFF7C4DFF),
                            containerColor = Color(0xFF1B1E36)
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
                Text("Box:", color = Color(0xFFA5B4FC), fontSize = 14.sp)
                InputChip(
                    selected = selectedBox == null,
                    onClick = { viewModel.onBoxFilterChanged(null) },
                    label = { Text("ALL", color = Color.White) },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = Color(0xFF10B981),
                        containerColor = Color(0xFF1B1E36)
                    ),
                    border = null
                )
                (1..5).forEach { boxNum ->
                    InputChip(
                        selected = selectedBox == boxNum,
                        onClick = { viewModel.onBoxFilterChanged(boxNum) },
                        label = { Text("$boxNum", color = Color.White) },
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = Color(0xFF10B981),
                            containerColor = Color(0xFF1B1E36)
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E36))
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
                                        color = Color.White
                                    )
                                    Text(
                                        text = "(${card.language})",
                                        fontSize = 12.sp,
                                        color = Color(0xFFA5B4FC)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = card.back,
                                    fontSize = 16.sp,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    BoxBadge(text = card.cardType, color = Color(0xFF2C3258))
                                    BoxBadge(text = "Box ${card.box}", color = Color(0xFF1B2E24))
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
fun BoxBadge(text: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
