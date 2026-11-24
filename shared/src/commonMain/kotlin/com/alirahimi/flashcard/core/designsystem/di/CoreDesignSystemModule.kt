package com.alirahimi.flashcard.core.designsystem.di

import com.alirahimi.flashcard.core.designsystem.theme.ThemeController
import org.koin.dsl.module

val coreDesignSystemModule = module {
    single { ThemeController(get()) }
}
