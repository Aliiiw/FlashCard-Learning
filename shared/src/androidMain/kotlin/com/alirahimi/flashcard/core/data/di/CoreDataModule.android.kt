package com.alirahimi.flashcard.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.alirahimi.flashcard.core.data.datasource.AndroidThemePreferencesDataSource
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import com.alirahimi.flashcard.db.FlashCardDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(FlashCardDatabase.Schema, androidContext(), "flashcard.db")
    }
    single<ThemePreferencesDataSource> {
        AndroidThemePreferencesDataSource(androidContext())
    }
}
