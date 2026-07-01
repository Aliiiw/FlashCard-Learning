package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alirahimi.flashcard.domain.model.FlashCard

@Composable
fun DashboardScreen(
    cards: List<FlashCard>,
    dueCardsCount: Int,
    themeMode: ThemeMode,
    customPresetColor: Color,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onCustomColorChanged: (Color) -> Unit,
    onStartReview: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToCardList: () -> Unit,
    onNavigateToImport: () -> Unit
) {
    val box1 = cards.count { it.box == 1 }
    val box2 = cards.count { it.box == 2 }
    val box3 = cards.count { it.box == 3 }
    val box4 = cards.count { it.box == 4 }
    val box5 = cards.count { it.box == 5 }

    val colors = LocalAppColors.current
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = {
                Text(
                    "Select Theme",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text("Theme Mode:", color = colors.textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            Button(
                                onClick = { onThemeModeChanged(mode) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (themeMode == mode) colors.primary else colors.borderColor.copy(
                                        alpha = 0.2f
                                    ),
                                    contentColor = if (themeMode == mode) Color.White else colors.textPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(mode.name, fontSize = 12.sp)
                            }
                        }
                    }

                    if (themeMode == ThemeMode.Custom) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Custom Color:", color = colors.textSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomThemePresets.forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(preset.color, shape = RoundedCornerShape(18.dp))
                                        .clickable { onCustomColorChanged(preset.color) }
                                        .padding(2.dp)
                                ) {
                                    if (customPresetColor == preset.color) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Color.White.copy(alpha = 0.4f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { showThemeDialog = false }) {
                            Text(
                                "OK",
                                color = colors.primary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            containerColor = colors.surface
        )
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
            Text(
                text = "Leitner Box",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showThemeDialog = true }) {
                    Text(
                        "🎨 Theme",
                        color = colors.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(onClick = onNavigateToImport) {
                    Text(
                        "📥 Import",
                        color = colors.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Due for Review",
                        fontSize = 16.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$dueCardsCount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (dueCardsCount > 0) colors.accent else colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onStartReview,
                        enabled = dueCardsCount > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            disabledContainerColor = colors.borderColor.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = "Start Review Session",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val columns = if (maxWidth < 600.dp) 2 else 5
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        BoxCard(
                            title = "Box 1",
                            count = box1,
                            subtitle = "Every 1 Day",
                            gradient = Brush.linearGradient(
                                listOf(
                                    Color(0xFFEF4444),
                                    Color(0xFFF87171)
                                )
                            )
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 2",
                            count = box2,
                            subtitle = "Every 2 Days",
                            gradient = Brush.linearGradient(
                                listOf(
                                    Color(0xFFF59E0B),
                                    Color(0xFFFBBF24)
                                )
                            )
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 3",
                            count = box3,
                            subtitle = "Every 4 Days",
                            gradient = Brush.linearGradient(
                                listOf(
                                    Color(0xFF10B981),
                                    Color(0xFF34D399)
                                )
                            )
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 4",
                            count = box4,
                            subtitle = "Every 8 Days",
                            gradient = Brush.linearGradient(
                                listOf(
                                    Color(0xFF3B82F6),
                                    Color(0xFF60A5FA)
                                )
                            )
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 5",
                            count = box5,
                            subtitle = "Every 16 Days",
                            gradient = Brush.linearGradient(
                                listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF818CF8)
                                )
                            )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToAddCard,
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(text = "Add Card", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToCardList,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.cardBackground),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(
                        text = "Word List",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun BoxCard(
    title: String,
    count: Int,
    subtitle: String,
    gradient: Brush
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$count",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
