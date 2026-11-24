package com.alirahimi.flashcard.core.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun LanguageOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    languageCode: String,
    modifier: Modifier = Modifier
)
