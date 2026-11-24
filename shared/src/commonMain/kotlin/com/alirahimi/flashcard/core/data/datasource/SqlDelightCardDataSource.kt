package com.alirahimi.flashcard.core.data.datasource

import com.alirahimi.flashcard.core.data.mapper.toFlashCard
import com.alirahimi.flashcard.core.domain.DataError
import com.alirahimi.flashcard.core.domain.EmptyResult
import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.db.FlashCardDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SqlDelightCardDataSource(
    private val database: FlashCardDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CardLocalDataSource {

    private val queries = database.flashCardDatabaseQueries

    override suspend fun getAllCards(): Result<List<FlashCard>, DataError.Local> {
        return safeDbCall {
            queries.selectAllCards().executeAsList().map { it.toFlashCard() }
        }
    }

    override suspend fun getCardsForReview(
        currentTimeEpochMs: Long
    ): Result<List<FlashCard>, DataError.Local> {
        return safeDbCall {
            queries.selectCardsForReview(currentTimeEpochMs)
                .executeAsList()
                .map { it.toFlashCard() }
        }
    }

    override suspend fun getCardById(id: Long): Result<FlashCard, DataError.Local> {
        return try {
            val entity = withContext(ioDispatcher) {
                queries.selectCardById(id).executeAsOneOrNull()
            }
            if (entity != null) {
                Result.Success(entity.toFlashCard())
            } else {
                Result.Error(DataError.Local.NOT_FOUND)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.toLocalDataError())
        }
    }

    override suspend fun insertCard(card: FlashCard): EmptyResult<DataError.Local> {
        return safeDbCall {
            queries.insertCard(
                front = card.front,
                back = card.back,
                language = card.language,
                box = card.box.toLong(),
                nextReviewTimeEpochMs = card.nextReviewTimeEpochMs,
                createdAtEpochMs = card.createdAtEpochMs,
                cardType = card.cardType
            )
        }
    }

    override suspend fun updateCardReview(
        id: Long,
        box: Int,
        nextReviewTimeEpochMs: Long
    ): EmptyResult<DataError.Local> {
        return safeDbCall {
            queries.updateCardReview(
                box = box.toLong(),
                nextReviewTimeEpochMs = nextReviewTimeEpochMs,
                id = id
            )
        }
    }

    override suspend fun deleteCard(id: Long): EmptyResult<DataError.Local> {
        return safeDbCall {
            queries.deleteCard(id)
        }
    }

    private suspend inline fun <T> safeDbCall(
        crossinline block: () -> T
    ): Result<T, DataError.Local> {
        return try {
            Result.Success(withContext(ioDispatcher) { block() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.toLocalDataError())
        }
    }

    private fun Throwable.toLocalDataError(): DataError.Local {
        val messageText = message.orEmpty()
        return if (messageText.contains("SQLITE_FULL", ignoreCase = true) ||
            messageText.contains("disk full", ignoreCase = true)
        ) {
            DataError.Local.DISK_FULL
        } else {
            DataError.Local.UNKNOWN
        }
    }
}
