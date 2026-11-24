package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePicker(onFileContentRead: (String) -> Unit): () -> Unit
