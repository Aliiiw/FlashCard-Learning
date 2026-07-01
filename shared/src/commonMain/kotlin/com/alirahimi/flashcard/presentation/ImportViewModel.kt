package com.alirahimi.flashcard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.domain.model.FlashCard
import com.alirahimi.flashcard.domain.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ImportResult {
    object Idle : ImportResult
    data class Success(val importedCount: Int, val skippedCount: Int) : ImportResult
    data class Error(val message: String) : ImportResult
}

class ImportViewModel(private val repository: CardRepository) : ViewModel() {
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _language = MutableStateFlow("EN")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _cardType = MutableStateFlow("Word")
    val cardType: StateFlow<String> = _cardType.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _importResult = MutableStateFlow<ImportResult>(ImportResult.Idle)
    val importResult: StateFlow<ImportResult> = _importResult.asStateFlow()

    fun onInputTextChanged(value: String) {
        _inputText.value = value
        _importResult.value = ImportResult.Idle
    }

    fun onLanguageChanged(value: String) {
        _language.value = value
        _importResult.value = ImportResult.Idle
    }

    fun onCardTypeChanged(value: String) {
        _cardType.value = value
        _importResult.value = ImportResult.Idle
    }

    fun clearResult() {
        _importResult.value = ImportResult.Idle
    }

    fun clearInputText() {
        _inputText.value = ""
        _importResult.value = ImportResult.Idle
    }

    fun importWords(textToImport: String, currentTimeEpochMs: Long) {
        viewModelScope.launch {
            _isImporting.value = true
            _importResult.value = ImportResult.Idle
            try {
                val existingCards = repository.getAllCards()
                val existingFronts = existingCards
                    .filter { it.language == _language.value }
                    .map { it.front.trim().lowercase() }
                    .toSet()

                var importedCount = 0
                var skippedCount = 0

                val lines = textToImport.split("\n")
                for (line in lines) {
                    val cleanedLine = line.trim()
                    if (cleanedLine.isEmpty()) continue

                    val separator = when {
                        cleanedLine.contains("->") -> "->"
                        cleanedLine.contains(" - ") -> " - "
                        cleanedLine.contains(":") -> ":"
                        cleanedLine.contains("=") -> "="
                        else -> null
                    }

                    if (separator == null) continue

                    val parts = cleanedLine.split(separator, limit = 2)
                    if (parts.size < 2) continue

                    val frontWord = parts[0].trim()
                    val backWord = parts[1].trim()

                    if (frontWord.isEmpty() || backWord.isEmpty()) continue

                    if (existingFronts.contains(frontWord.lowercase())) {
                        skippedCount++
                        continue
                    }

                    val card = FlashCard(
                        id = 0L,
                        front = frontWord,
                        back = backWord,
                        language = _language.value,
                        box = 1,
                        nextReviewTimeEpochMs = currentTimeEpochMs,
                        createdAtEpochMs = currentTimeEpochMs,
                        cardType = _cardType.value
                    )
                    repository.insertCard(card)
                    importedCount++
                }

                _importResult.value = ImportResult.Success(importedCount, skippedCount)
            } catch (e: Exception) {
                _importResult.value = ImportResult.Error(e.message ?: "Failed to import cards")
            } finally {
                _isImporting.value = false
            }
        }
    }
}
