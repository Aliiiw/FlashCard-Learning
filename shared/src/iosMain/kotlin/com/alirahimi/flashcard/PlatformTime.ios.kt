package com.alirahimi.flashcard

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getCurrentTimeEpochMs(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}
