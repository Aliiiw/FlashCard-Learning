package com.alirahimi.flashcard.di

import assertk.assertThat
import assertk.assertions.isNotNull
import com.alirahimi.flashcard.core.designsystem.di.coreDesignSystemModule
import com.alirahimi.flashcard.core.designsystem.theme.ThemeController
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.TimeProvider
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.datasource.ThemePreferencesDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.cards.domain.usecase.AddCardUseCase
import com.alirahimi.flashcard.feature.cards.presentation.di.cardsPresentationModule
import com.alirahimi.flashcard.feature.dashboard.presentation.di.dashboardPresentationModule
import com.alirahimi.flashcard.feature.importcards.domain.usecase.ImportCardsUseCase
import com.alirahimi.flashcard.feature.importcards.presentation.di.importPresentationModule
import com.alirahimi.flashcard.feature.review.domain.usecase.ReviewCardUseCase
import com.alirahimi.flashcard.feature.review.presentation.di.reviewPresentationModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test

class KoinGraphTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun koinGraphResolvesCoreAndFeatureDependencies() {
        val testOverrides = module {
            single<CardLocalDataSource> {
                object : CardLocalDataSource {
                    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> = Result.Success(emptyList())
                    override suspend fun getCardsForReview(currentTimeEpochMs: Long): Result<List<FlashCard>, DataError.Local> = Result.Success(emptyList())
                    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> = Result.Error(DataError.Local.NOT_FOUND)
                    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> = Result.Success(Unit)
                    override suspend fun updateCardReview(id: Long, box: Int, nextReviewTimeEpochMs: Long): EmptyResult<DataError.Local> = Result.Success(Unit)
                    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> = Result.Success(Unit)
                }
            }
            single<TimeProvider> {
                object : TimeProvider {
                    override fun currentTimeEpochMs(): Long = 0L
                }
            }
            single<ThemePreferencesDataSource> {
                object : ThemePreferencesDataSource {
                    private var mode: String? = null
                    private var color: Int? = null
                    override fun getThemeMode(): String? = mode
                    override fun saveThemeMode(mode: String) { this.mode = mode }
                    override fun getCustomColorArgb(): Int? = color
                    override fun saveCustomColorArgb(colorArgb: Int) { this.color = colorArgb }
                }
            }
        }

        val app = startKoin {
            modules(
                testOverrides,
                coreDesignSystemModule,
                dashboardPresentationModule,
                cardsPresentationModule,
                reviewPresentationModule,
                importPresentationModule
            )
        }

        assertThat(app.koin.get<ThemeController>()).isNotNull()
        assertThat(app.koin.get<AddCardUseCase>()).isNotNull()
        assertThat(app.koin.get<ReviewCardUseCase>()).isNotNull()
        assertThat(app.koin.get<ImportCardsUseCase>()).isNotNull()
    }
}
