package com.alirahimi.flashcard.core.data.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.alirahimi.flashcard.core.data.datasource.IosThemePreferencesDataSource
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import com.alirahimi.flashcard.db.FlashCardDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformCoreDataModule: Module = module {
    single<SqlDriver> {
        NativeSqliteDriver(FlashCardDatabase.Schema, "flashcard.db")
    }
    singleOf(::IosThemePreferencesDataSource) { bind<ThemePreferencesDataSource>() }
}
