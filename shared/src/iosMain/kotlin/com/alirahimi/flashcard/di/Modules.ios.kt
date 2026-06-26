package com.alirahimi.flashcard.di

import com.alirahimi.flashcard.data.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        DatabaseDriverFactory().createDriver()
    }
}
