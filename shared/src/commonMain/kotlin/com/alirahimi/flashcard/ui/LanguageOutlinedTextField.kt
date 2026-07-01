package com.alirahimi.flashcard.ui

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

@Composable
expect fun Modifier.clearFocusOnTap(): Modifier
