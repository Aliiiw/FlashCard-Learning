package com.alirahimi.flashcard.feature.review.presentation

import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.core.presentation.UiText

data class ReviewCardUi(
    val id: Long,
    val front: String,
    val back: String,
    val language: String,
    val cardType: String,
    val boxLabel: String
)

fun FlashCard.toReviewCardUi(): ReviewCardUi = ReviewCardUi(
    id = id,
    front = front,
    back = back,
    language = language,
    cardType = cardType,
    boxLabel = "Box $box"
)

data class ReviewState(
    val currentCard: ReviewCardUi? = null,
    val displayedAnswer: String = "",
    val currentIndex: Int = 0,
    val totalCards: Int = 0,
    val isAnswerVisible: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
) {
    val progress: Float
        get() = if (totalCards > 0) (currentIndex + 1).toFloat() / totalCards else 0f

    val progressLabel: String
        get() = "Card ${currentIndex + 1} of $totalCards"
}
