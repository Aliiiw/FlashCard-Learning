package com.alirahimi.flashcard.ui

import androidx.compose.runtime.Composable

@Composable
expect fun rememberFilePicker(onFileContentRead: (String) -> Unit): () -> Unit
