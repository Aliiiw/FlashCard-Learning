package com.alirahimi.flashcard.feature.dashboard.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alirahimi.flashcard.core.designsystem.component.AppCard
import com.alirahimi.flashcard.core.designsystem.component.BoxCard
import com.alirahimi.flashcard.core.designsystem.component.PrimaryButton
import com.alirahimi.flashcard.core.designsystem.theme.AppTheme
import com.alirahimi.flashcard.core.designsystem.theme.CustomThemePresets
import com.alirahimi.flashcard.core.designsystem.theme.ThemeMode
import com.alirahimi.flashcard.core.presentation.ObserveAsEvents
import com.alirahimi.flashcard.core.presentation.ObserveLifecycle
import com.alirahimi.flashcard.core.presentation.resources.Res
import com.alirahimi.flashcard.core.presentation.resources.cd_import_cards
import com.alirahimi.flashcard.core.presentation.resources.cd_select_custom_color
import com.alirahimi.flashcard.core.presentation.resources.cd_theme_settings
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoot(
    onStartReview: () -> Unit,
    onNavigateToAddCard: () -> Unit,
    onNavigateToCardList: () -> Unit,
    onNavigateToImport: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveLifecycle(
        onResume = { viewModel.onAction(DashboardAction.OnRefresh) }
    )

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            DashboardEvent.NavigateToReview -> onStartReview()
            DashboardEvent.NavigateToAddCard -> onNavigateToAddCard()
            DashboardEvent.NavigateToCardList -> onNavigateToCardList()
            DashboardEvent.NavigateToImport -> onNavigateToImport()
            is DashboardEvent.ShowError -> Unit
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val themeDescription = stringResource(Res.string.cd_theme_settings)
    val importDescription = stringResource(Res.string.cd_import_cards)

    if (state.isThemeDialogVisible) {
        ThemeSelectionDialog(
            themeMode = state.themeMode,
            customPrimaryColor = state.customPrimaryColor,
            onThemeModeSelect = { onAction(DashboardAction.OnThemeModeSelect(it)) },
            onCustomColorSelect = { onAction(DashboardAction.OnCustomColorSelect(it)) },
            onDismiss = { onAction(DashboardAction.OnDismissThemeDialog) }
        )
    }

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
            Text(
                text = "Leitner Box",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onAction(DashboardAction.OnOpenThemeDialog) },
                    modifier = Modifier.semantics { contentDescription = themeDescription }
                ) {
                    Text(
                        text = "🎨 Theme",
                        color = colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(
                    onClick = { onAction(DashboardAction.OnImportClick) },
                    modifier = Modifier.semantics { contentDescription = importDescription }
                ) {
                    Text(
                        text = "📥 Import",
                        color = colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Due for Review",
                        fontSize = 16.sp,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${state.dueCardsCount}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (state.dueCardsCount > 0) colorScheme.secondary else colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Start Review Session",
                        onClick = { onAction(DashboardAction.OnStartReviewClick) },
                        enabled = state.dueCardsCount > 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            state.error?.let { errorText ->
                Text(
                    text = errorText.asString(),
                    color = colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
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
                    items(
                        items = state.boxes,
                        key = { it.boxNumber }
                    ) { box ->
                        BoxCard(
                            title = box.title,
                            count = box.count,
                            subtitle = box.subtitle,
                            gradient = boxGradient(box.boxNumber)
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
                    onClick = { onAction(DashboardAction.OnAddCardClick) },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Add Card",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onAction(DashboardAction.OnWordListClick) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Word List",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    themeMode: ThemeMode,
    customPrimaryColor: Color,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onCustomColorSelect: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Theme",
                color = colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Theme Mode:",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = themeMode == mode
                        Button(
                            onClick = { onThemeModeSelect(mode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.2f),
                                contentColor = if (isSelected) Color.White else colorScheme.onSurface
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
                    Text(
                        text = "Custom Color:",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomThemePresets.forEach { preset ->
                            val colorDesc = stringResource(Res.string.cd_select_custom_color, preset.name)
                            val isSelected = customPrimaryColor == preset.color
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .then(
                                        if (isSelected) {
                                            Modifier.border(2.5.dp, Color.White, CircleShape)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .padding(3.dp)
                                    .background(preset.color, shape = CircleShape)
                                    .semantics { contentDescription = colorDesc }
                                    .clickable { onCustomColorSelect(preset.color) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "OK",
                            color = colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = colorScheme.surface
    )
}

private fun boxGradient(boxNumber: Int): Brush = when (boxNumber) {
    1 -> Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFF87171)))
    2 -> Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFBBF24)))
    3 -> Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
    4 -> Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)))
    else -> Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF818CF8)))
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    AppTheme {
        DashboardScreen(
            state = DashboardState(
                boxes = DashboardState.defaultBoxes(mapOf(1 to 12, 2 to 5, 3 to 8, 4 to 3, 5 to 2)),
                dueCardsCount = 7
            ),
            onAction = {}
        )
    }
}
