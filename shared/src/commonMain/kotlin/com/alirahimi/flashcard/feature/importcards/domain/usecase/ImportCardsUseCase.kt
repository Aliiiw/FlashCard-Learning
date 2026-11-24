package com.alirahimi.flashcard.feature.importcards.domain.usecase

import com.alirahimi.flashcard.core.domain.Result
import com.alirahimi.flashcard.core.domain.datasource.CardLocalDataSource
import com.alirahimi.flashcard.core.domain.model.FlashCard
import com.alirahimi.flashcard.feature.importcards.domain.ImportError
import com.alirahimi.flashcard.feature.importcards.domain.ImportSummary

class ImportCardsUseCase(
    private val cardLocalDataSource: CardLocalDataSource
) {
    suspend operator fun invoke(
        rawText: String,
        language: String,
        cardType: String,
        currentTimeEpochMs: Long
    ): Result<ImportSummary, ImportError> {
        if (rawText.isBlank()) {
            return Result.Error(ImportError.Validation.EMPTY_INPUT)
        }

        val parsedPairs = parseLines(rawText)
        if (parsedPairs.isEmpty()) {
            return Result.Error(ImportError.Validation.NO_VALID_LINES)
        }

        val existingCards = when (val allResult = cardLocalDataSource.getAllCards()) {
            is Result.Success -> allResult.data
            is Result.Error -> return Result.Error(ImportError.Storage(allResult.error))
        }

        val existingFronts = existingCards
            .filter { it.language == language }
            .map { it.front.trim().lowercase() }
            .toMutableSet()

        var importedCount = 0
        var skippedCount = 0

        for ((frontWord, backWord) in parsedPairs) {
            val normalizedFront = frontWord.lowercase()
            if (!existingFronts.add(normalizedFront)) {
                skippedCount++
                continue
            }

            val card = FlashCard(
                id = 0L,
                front = frontWord,
                back = backWord,
                language = language,
                box = 1,
                nextReviewTimeEpochMs = currentTimeEpochMs,
                createdAtEpochMs = currentTimeEpochMs,
                cardType = cardType
            )

            when (val insertResult = cardLocalDataSource.insertCard(card)) {
                is Result.Success -> importedCount++
                is Result.Error -> return Result.Error(ImportError.Storage(insertResult.error))
            }
        }

        return Result.Success(
            ImportSummary(
                importedCount = importedCount,
                skippedCount = skippedCount
            )
        )
    }

    private fun parseLines(rawText: String): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        for (line in rawText.split("\n")) {
            val cleanedLine = line.trim()
            if (cleanedLine.isEmpty()) continue

            val separator = when {
                cleanedLine.contains("->") -> "->"
                cleanedLine.contains(" - ") -> " - "
                cleanedLine.contains(":") -> ":"
                cleanedLine.contains("=") -> "="
                else -> null
            } ?: continue

            val parts = cleanedLine.split(separator, limit = 2)
            if (parts.size < 2) continue

            val frontWord = parts[0].trim()
            val backWord = parts[1].trim()
            if (frontWord.isNotEmpty() && backWord.isNotEmpty()) {
                result.add(frontWord to backWord)
            }
        }
        return result
    }
}
