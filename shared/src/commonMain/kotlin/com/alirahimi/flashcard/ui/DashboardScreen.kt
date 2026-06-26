package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onStartReview: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToCardList: () -> Unit
) {
    val box1 = cards.count { it.box == 1 }
    val box2 = cards.count { it.box == 2 }
    val box3 = cards.count { it.box == 3 }
    val box4 = cards.count { it.box == 4 }
    val box5 = cards.count { it.box == 5 }

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
            Text(
                text = "Leitner Box",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E36))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Due for Review",
                        fontSize = 16.sp,
                        color = Color(0xFFA5B4FC)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$dueCardsCount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (dueCardsCount > 0) Color(0xFF10B981) else Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onStartReview,
                        enabled = dueCardsCount > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7C4DFF),
                            disabledContainerColor = Color(0xFF2C3258)
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
                            gradient = Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171)))
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 2",
                            count = box2,
                            subtitle = "Every 2 Days",
                            gradient = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 3",
                            count = box3,
                            subtitle = "Every 4 Days",
                            gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 4",
                            count = box4,
                            subtitle = "Every 8 Days",
                            gradient = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)))
                        )
                    }
                    item {
                        BoxCard(
                            title = "Box 5",
                            count = box5,
                            subtitle = "Every 16 Days",
                            gradient = Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF818CF8)))
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7C4DFF)),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(text = "Add Card", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNavigateToCardList,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1E36)),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text(text = "Word List", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
