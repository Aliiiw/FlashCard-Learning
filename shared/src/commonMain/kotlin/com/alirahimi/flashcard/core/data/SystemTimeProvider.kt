package com.alirahimi.flashcard.core.data

import com.alirahimi.flashcard.core.domain.TimeProvider

expect fun currentPlatformTimeEpochMs(): Long

class SystemTimeProvider : TimeProvider {
    override fun currentTimeEpochMs(): Long = currentPlatformTimeEpochMs()
}
