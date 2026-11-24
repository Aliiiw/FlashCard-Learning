package com.alirahimi.flashcard.core.domain

interface TimeProvider {
    fun currentTimeEpochMs(): Long
}
