package com.alirahimi.flashcard.feature.cards.presentation.list

import androidx.compose.runtime.Stable
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.core.presentation.UiText

data class FlashCardUi(
    val id: Long,
    val front: String,
    val back: String,
    val language: String,
    val boxLabel: String,
    val box: Int,
    val cardType: String
)

fun FlashCard.toFlashCardUi(): FlashCardUi = FlashCardUi(
    id = id,
    front = front,
    back = back,
    language = language,
    boxLabel = "Box $box",
    box = box,
    cardType = cardType
)

@Stable
data class CardListState(
    val filteredCards: List<FlashCardUi> = emptyList(),
    val searchQuery: String = "",
    val selectedLanguageFilter: String = "ALL",
    val selectedBoxFilter: Int? = null,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
