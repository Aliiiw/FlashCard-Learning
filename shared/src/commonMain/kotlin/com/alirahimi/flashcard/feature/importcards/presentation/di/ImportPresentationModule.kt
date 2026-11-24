package com.alirahimi.flashcard.feature.importcards.presentation.di

import com.alirahimi.flashcard.feature.importcards.domain.usecase.ImportCardsUseCase
import com.alirahimi.flashcard.feature.importcards.presentation.ImportViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val importPresentationModule = module {
    factoryOf(::ImportCardsUseCase)
    viewModelOf(::ImportViewModel)
}
