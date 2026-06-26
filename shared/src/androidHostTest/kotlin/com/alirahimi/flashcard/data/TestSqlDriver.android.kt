package com.alirahimi.flashcard.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.alirahimi.flashcard.db.FlashCardDatabase

actual fun createTestSqlDriver(): SqlDriver {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    FlashCardDatabase.Schema.create(driver)
    return driver
}
