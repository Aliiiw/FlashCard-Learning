package com.alirahimi.flashcard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alirahimi.flashcard.getCurrentTimeEpochMs
import com.alirahimi.flashcard.presentation.AddCardViewModel

@Composable
fun AddCardScreen(
    viewModel: AddCardViewModel,
    onBack: () -> Unit
) {
    val word by viewModel.word.collectAsState()
    val translation by viewModel.translation.collectAsState()
    val language by viewModel.language.collectAsState()
    val cardType by viewModel.cardType.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val wordLocale = if (language == "EN") Locale("en") else Locale("fr")

    LaunchedEffect(isSaved) {
        if (isSaved) {
            viewModel.clearState()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearState()
        }
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
                    text = "Add Card",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Language",
                color = Color(0xFFA5B4FC),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onLanguageChanged("EN") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (language == "EN") Color(0xFF7C4DFF) else Color(0xFF1B1E36)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("English")
                }
                Button(
                    onClick = { viewModel.onLanguageChanged("FR") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (language == "FR") Color(0xFF7C4DFF) else Color(0xFF1B1E36)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("French")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Card Type",
                color = Color(0xFFA5B4FC),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onCardTypeChanged("Word") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cardType == "Word") Color(0xFF10B981) else Color(0xFF1B1E36)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Word")
                }
                Button(
                    onClick = { viewModel.onCardTypeChanged("Phrase") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (cardType == "Phrase") Color(0xFF10B981) else Color(0xFF1B1E36)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Phrase / Idiom")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = word,
                onValueChange = { viewModel.onWordChanged(it) },
                label = { Text("Word / Phrase", color = Color(0xFFA5B4FC)) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    hintLocales = LocaleList(wordLocale)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedBorderColor = Color(0xFF2C3258),
                    cursorColor = Color(0xFF7C4DFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = translation,
                onValueChange = { viewModel.onTranslationChanged(it) },
                label = { Text("Farsi Translation", color = Color(0xFFA5B4FC)) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    hintLocales = LocaleList(Locale("fa"))
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C4DFF),
                    unfocusedBorderColor = Color(0xFF2C3258),
                    cursorColor = Color(0xFF7C4DFF)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { viewModel.saveCard(getCurrentTimeEpochMs()) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save Card", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
