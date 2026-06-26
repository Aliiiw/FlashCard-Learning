package com.alirahimi.flashcard.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.alirahimi.flashcard.db.FlashCardDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(FlashCardDatabase.Schema, "flashcard.db")
    }
}
