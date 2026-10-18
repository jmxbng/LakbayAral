package com.example.lakbayaral.data.progression

/**
 * The one authoritative municipality progression for Lakbay Aral.
 *
 * The ids intentionally match [LocalDataSource] records instead of adding
 * aliases such as "SanFernando" or "Santo Thomas".  Display text is resolved
 * from those records by the UI, so an existing city name (for example Angeles
 * City) is retained everywhere it is already shown.
 */
object MunicipalityProgression {
    const val PASSING_PERCENTAGE = 70

    data class Entry(
        val municipalityId: String,
        val sequenceName: String
    )

    val sequence: List<Entry> = listOf(
        Entry("lubao", "Lubao"),
        Entry("floridablanca", "Floridablanca"),
        Entry("porac", "Porac"),
        Entry("angeles_city", "Angeles"),
        Entry("mabalacat", "Mabalacat"),
        Entry("magalang", "Magalang"),
        Entry("arayat", "Arayat"),
        Entry("santa_ana", "Santa Ana"),
        Entry("mexico", "Mexico"),
        Entry("san_luis", "San Luis"),
        Entry("candaba", "Candaba"),
        Entry("san_simon", "San Simon"),
        Entry("apalit", "Apalit"),
        Entry("macabebe", "Macabebe"),
        Entry("masantol", "Masantol"),
        Entry("minalin", "Minalin"),
        Entry("sasmuan", "Sasmuan"),
        Entry("guagua", "Guagua"),
        Entry("santa_rita", "Santa Rita"),
        Entry("bacolor", "Bacolor"),
        Entry("san_fernando", "San Fernando"),
        Entry("santo_tomas", "Santo Tomas")
    )

    val initialMunicipalityId: String = sequence.first().municipalityId
    val finalMunicipalityId: String = sequence.last().municipalityId

    init {
        check(sequence.map { it.municipalityId }.distinct().size == sequence.size) {
            "Municipality progression contains duplicate municipality ids."
        }
    }

    fun contains(municipalityId: String): Boolean =
        sequence.any { it.municipalityId == municipalityId }

    fun nextMunicipalityId(completedMunicipalityId: String): String? {
        val completedIndex = sequence.indexOfFirst { it.municipalityId == completedMunicipalityId }
        return if (completedIndex >= 0) {
            sequence.getOrNull(completedIndex + 1)?.municipalityId
        } else {
            null
        }
    }

    fun previousMunicipalityId(municipalityId: String): String? {
        val index = sequence.indexOfFirst { it.municipalityId == municipalityId }
        return if (index > 0) sequence[index - 1].municipalityId else null
    }

    /**
     * Fails fast during app startup if content and progression configuration
     * drift apart. This prevents a newly added map record from accidentally
     * becoming independently unlockable outside the configured sequence.
     */
    fun requireExactMunicipalityIds(contentMunicipalityIds: Collection<String>) {
        val configuredIds = sequence.mapTo(linkedSetOf()) { it.municipalityId }
        check(contentMunicipalityIds.toSet() == configuredIds) {
            "Municipality content ids must exactly match the configured progression."
        }
    }

    /**
     * A quiz may be attempted only for an unlocked sequence municipality whose
     * prior required quizzes are already completed.  Existing out-of-sequence
     * unlocks are intentionally retained, but cannot be used to skip quizzes.
     */
    fun accessFor(
        municipalityId: String,
        unlockedMunicipalityIds: Set<String>,
        completedMunicipalityIds: Set<String>
    ): QuizAccess = when {
        !contains(municipalityId) -> QuizAccess.UnknownMunicipality
        municipalityId !in unlockedMunicipalityIds -> QuizAccess.Locked
        municipalityId in completedMunicipalityIds -> QuizAccess.Completed
        else -> {
            val missingPrerequisite = sequence
                .takeWhile { it.municipalityId != municipalityId }
                .firstOrNull { it.municipalityId !in completedMunicipalityIds }
                ?.municipalityId
            if (missingPrerequisite == null) {
                QuizAccess.Allowed
            } else {
                QuizAccess.PrerequisiteIncomplete(missingPrerequisite)
            }
        }
    }

    /**
     * Computes the only legal post-quiz state change.  It never selects a
     * later municipality when the immediate next one already exists.
     */
    fun outcomeForPassedQuiz(
        municipalityId: String,
        unlockedMunicipalityIds: Set<String>,
        completedMunicipalityIds: Set<String>
    ): QuizProgressionOutcome = when (
        val access = accessFor(municipalityId, unlockedMunicipalityIds, completedMunicipalityIds)
    ) {
        QuizAccess.Allowed -> {
            val nextMunicipalityId = nextMunicipalityId(municipalityId)
            when {
                nextMunicipalityId == null -> QuizProgressionOutcome.SequenceAlreadyComplete
                nextMunicipalityId == finalMunicipalityId && nextMunicipalityId !in unlockedMunicipalityIds ->
                    QuizProgressionOutcome.FinalMunicipalityUnlocked(nextMunicipalityId)
                nextMunicipalityId !in unlockedMunicipalityIds ->
                    QuizProgressionOutcome.NextMunicipalityUnlocked(nextMunicipalityId)
                nextMunicipalityId == finalMunicipalityId ->
                    QuizProgressionOutcome.FinalMunicipalityAlreadyUnlocked(nextMunicipalityId)
                else -> QuizProgressionOutcome.NextMunicipalityAlreadyUnlocked(nextMunicipalityId)
            }
        }
        QuizAccess.Completed -> QuizProgressionOutcome.AlreadyCompleted
        is QuizAccess.PrerequisiteIncomplete -> QuizProgressionOutcome.Rejected(access)
        QuizAccess.Locked, QuizAccess.UnknownMunicipality -> QuizProgressionOutcome.Rejected(access)
    }
}

sealed interface QuizAccess {
    data object Allowed : QuizAccess
    data object Completed : QuizAccess
    data object Locked : QuizAccess
    data object UnknownMunicipality : QuizAccess
    data class PrerequisiteIncomplete(val municipalityId: String) : QuizAccess
}

sealed interface QuizProgressionOutcome {
    data object QuizFailed : QuizProgressionOutcome
    data object AlreadyCompleted : QuizProgressionOutcome
    data object SequenceAlreadyComplete : QuizProgressionOutcome
    data class NextMunicipalityUnlocked(val municipalityId: String) : QuizProgressionOutcome
    data class NextMunicipalityAlreadyUnlocked(val municipalityId: String) : QuizProgressionOutcome
    data class FinalMunicipalityUnlocked(val municipalityId: String) : QuizProgressionOutcome
    data class FinalMunicipalityAlreadyUnlocked(val municipalityId: String) : QuizProgressionOutcome
    data class Rejected(val access: QuizAccess) : QuizProgressionOutcome
    data class InvalidSubmission(val reason: String) : QuizProgressionOutcome
}
