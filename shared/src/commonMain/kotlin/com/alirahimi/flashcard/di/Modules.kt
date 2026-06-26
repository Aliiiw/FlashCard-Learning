package com.alirahimi.flashcard.di

import com.alirahimi.flashcard.db.FlashCardDatabase
import com.alirahimi.flashcard.data.repository.CardRepositoryImpl
import com.alirahimi.flashcard.domain.repository.CardRepository
import com.alirahimi.flashcard.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.domain.usecase.GetAllCardsUseCase
import com.alirahimi.flashcard.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.domain.usecase.ReviewCardUseCase
import com.alirahimi.flashcard.presentation.AddCardViewModel
import com.alirahimi.flashcard.presentation.CardListViewModel
import com.alirahimi.flashcard.presentation.ReviewViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val commonModule = module {
    single {
        FlashCardDatabase(get())
    }
    single<CardRepository> {
        CardRepositoryImpl(get())
    }
    factory { AddCardUseCase(get()) }
    factory { ReviewCardUseCase(get()) }
    factory { GetCardsForReviewUseCase(get()) }
    factory { GetAllCardsUseCase(get()) }
    factory { AddCardViewModel(get()) }
    factory { ReviewViewModel(get(), get()) }
    factory { CardListViewModel(get(), get()) }
}
