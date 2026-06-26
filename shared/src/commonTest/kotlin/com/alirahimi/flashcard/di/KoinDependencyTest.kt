package com.alirahimi.flashcard.di

import com.alirahimi.flashcard.data.createTestSqlDriver
import com.alirahimi.flashcard.db.FlashCardDatabase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class KoinDependencyTest {
    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testDatabaseIsResolved() {
        val app = startKoin {
            modules(
                commonModule,
                module {
                    single { createTestSqlDriver() }
                }
            )
        }
        val db = app.koin.get<FlashCardDatabase>()
        assertNotNull(db)
    }
}
