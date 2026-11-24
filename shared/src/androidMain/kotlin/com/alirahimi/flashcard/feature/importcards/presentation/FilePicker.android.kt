package com.alirahimi.flashcard.feature.importcards.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberFilePicker(onFileContentRead: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val content = context.contentResolver.openInputStream(it)?.use { inputStream ->
                    inputStream.bufferedReader().use { reader -> reader.readText() }
                }
                if (content != null) {
                    onFileContentRead(content)
                }
            } catch (_: Exception) {
            }
        }
    }
    return { launcher.launch("text/*") }
}
