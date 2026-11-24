package com.alirahimi.flashcard.core.data.di

import com.alirahimi.flashcard.core.data.SystemTimeProvider
import com.alirahimi.flashcard.core.data.datasource.SqlDelightCardDataSource
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.db.FlashCardDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    single { FlashCardDatabase(get()) }
    single<CardLocalDataSource> { SqlDelightCardDataSource(get()) }
    singleOf(::SystemTimeProvider) { bind<TimeProvider>() }
}
