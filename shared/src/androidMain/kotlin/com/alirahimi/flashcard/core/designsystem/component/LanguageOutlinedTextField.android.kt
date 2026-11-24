package com.alirahimi.flashcard.core.designsystem.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp

@Composable
actual fun LanguageOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    languageCode: String,
    modifier: Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colorScheme.onSurfaceVariant) },
        keyboardOptions = KeyboardOptions.Default.copy(
            hintLocales = LocaleList(Locale(languageCode))
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline,
            cursorColor = colorScheme.primary,
            focusedTextColor = colorScheme.onBackground,
            unfocusedTextColor = colorScheme.onBackground
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}
