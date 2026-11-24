package com.alirahimi.flashcard.core.data.mapper

import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.db.CardEntity

fun CardEntity.toFlashCard(): FlashCard = FlashCard(
    id = id,
    front = front,
    back = back,
    language = language,
    box = box.toInt(),
    nextReviewTimeEpochMs = nextReviewTimeEpochMs,
    createdAtEpochMs = createdAtEpochMs,
    cardType = cardType
)
