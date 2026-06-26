package com.alirahimi.flashcard.data

import com.alirahimi.flashcard.db.FlashCardDatabase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatabaseTest {
    private lateinit var database: FlashCardDatabase

    @BeforeTest
    fun setUp() {
        val driver = createTestSqlDriver()
        database = FlashCardDatabase(driver)
    }

    @Test
    fun testInsertAndSelectCard() {
        database.flashCardDatabaseQueries.insertCard(
            front = "hello",
            back = "سلام",
            language = "EN",
            box = 1,
            nextReviewTimeEpochMs = 1000L,
            createdAtEpochMs = 500L,
            cardType = "Word"
        )
        val cards = database.flashCardDatabaseQueries.selectAllCards().executeAsList()
        assertEquals(1, cards.size)
        assertEquals("hello", cards[0].front)
        assertEquals("سلام", cards[0].back)
    }
}
