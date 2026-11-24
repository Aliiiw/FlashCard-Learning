package com.alirahimi.flashcard.core.data

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun currentPlatformTimeEpochMs(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
