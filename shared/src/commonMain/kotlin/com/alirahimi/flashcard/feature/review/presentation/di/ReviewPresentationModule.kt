package com.alirahimi.flashcard.feature.review.presentation.di

import com.alirahimi.flashcard.feature.review.domain.usecase.GetCardsForReviewUseCase
import com.alirahimi.flashcard.feature.review.domain.usecase.ReviewCardUseCase
import com.alirahimi.flashcard.feature.review.presentation.ReviewViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val reviewPresentationModule = module {
    factoryOf(::GetCardsForReviewUseCase)
    factoryOf(::ReviewCardUseCase)
    viewModelOf(::ReviewViewModel)
}
