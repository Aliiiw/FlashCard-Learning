package com.alirahimi.flashcard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alirahimi.flashcard.domain.usecase.AddCardUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCardViewModel(private val addCardUseCase: AddCardUseCase) : ViewModel() {
    private val _word = MutableStateFlow("")
    val word: StateFlow<String> = _word.asStateFlow()

    private val _translation = MutableStateFlow("")
    val translation: StateFlow<String> = _translation.asStateFlow()

    private val _language = MutableStateFlow("EN")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _cardType = MutableStateFlow("Word")
    val cardType: StateFlow<String> = _cardType.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onWordChanged(value: String) {
        _word.value = value
        _errorMessage.value = null
    }

    fun onTranslationChanged(value: String) {
        _translation.value = value
        _errorMessage.value = null
    }

    fun onLanguageChanged(value: String) {
        _language.value = value
    }

    fun onCardTypeChanged(value: String) {
        _cardType.value = value
    }

    fun saveCard(currentTimeEpochMs: Long) {
        viewModelScope.launch {
            try {
                addCardUseCase(
                    front = _word.value,
                    back = _translation.value,
                    language = _language.value,
                    cardType = _cardType.value,
                    currentTimeEpochMs = currentTimeEpochMs
                )
                _isSaved.value = true
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error saving card"
                _isSaved.value = false
            }
        }
    }

    fun clearState() {
        _word.value = ""
        _translation.value = ""
        _isSaved.value = false
        _errorMessage.value = null
    }
}
