package com.alirahimi.flashcard.feature.cards.presentation.di

import com.alirahimi.flashcard.feature.cards.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.feature.cards.domain.usecase.DeleteCardUseCase
import com.alirahimi.flashcard.feature.cards.domain.usecase.GetAllCardsUseCase
import com.alirahimi.flashcard.feature.cards.presentation.addcard.AddCardViewModel
import com.alirahimi.flashcard.feature.cards.presentation.list.CardListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cardsPresentationModule = module {
    factoryOf(::AddCardUseCase)
    factoryOf(::GetAllCardsUseCase)
    factoryOf(::DeleteCardUseCase)
    viewModelOf(::AddCardViewModel)
    viewModelOf(::CardListViewModel)
}
