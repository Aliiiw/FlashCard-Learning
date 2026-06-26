package com.alirahimi.flashcard.domain.model

data class FlashCard(
    val id: Long,
    val front: String,
    val back: String,
    val language: String,
    val box: Int,
    val nextReviewTimeEpochMs: Long,
    val createdAtEpochMs: Long,
    val cardType: String
)
