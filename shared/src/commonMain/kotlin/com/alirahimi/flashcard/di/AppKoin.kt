package com.alirahimi.flashcard.di

import com.alirahimi.flashcard.core.data.di.coreDataModule
import com.alirahimi.flashcard.core.data.di.platformCoreDataModule
import com.alirahimi.flashcard.core.designsystem.di.coreDesignSystemModule
import com.alirahimi.flashcard.feature.cards.presentation.di.cardsPresentationModule
import com.alirahimi.flashcard.feature.dashboard.presentation.di.dashboardPresentationModule
import com.alirahimi.flashcard.feature.importcards.presentation.di.importPresentationModule
import com.alirahimi.flashcard.feature.review.presentation.di.reviewPresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

val appModules = listOf(
    coreDataModule,
    platformCoreDataModule,
    coreDesignSystemModule,
    dashboardPresentationModule,
    cardsPresentationModule,
    reviewPresentationModule,
    importPresentationModule
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(appModules)
    }
}
