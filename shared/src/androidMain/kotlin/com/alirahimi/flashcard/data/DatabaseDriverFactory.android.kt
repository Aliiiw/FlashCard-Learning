package com.alirahimi.flashcard.data

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.alirahimi.flashcard.db.FlashCardDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(FlashCardDatabase.Schema, context, "flashcard.db")
    }
}
