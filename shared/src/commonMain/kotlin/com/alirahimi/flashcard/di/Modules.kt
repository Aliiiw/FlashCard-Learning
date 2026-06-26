package com.alirahimi.flashcard.di

import com.alirahimi.flashcard.db.FlashCardDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val commonModule = module {
    single {
        FlashCardDatabase(get())
    }
}
