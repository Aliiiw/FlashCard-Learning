package com.alirahimi.flashcard.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.alirahimi.flashcard.db.FlashCardDatabase

actual fun createTestSqlDriver(): SqlDriver {
    return NativeSqliteDriver(FlashCardDatabase.Schema, "test.db")
}
