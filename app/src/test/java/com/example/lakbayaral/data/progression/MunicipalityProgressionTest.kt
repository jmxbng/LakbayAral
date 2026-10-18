package com.example.lakbayaral.data.progression

import com.example.lakbayaral.data.repository.LocalDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MunicipalityProgressionTest {

    @Test
    fun `uses the required twenty two municipality sequence`() {
        assertEquals(
            listOf(
                "Lubao", "Floridablanca", "Porac", "Angeles", "Mabalacat", "Magalang",
                "Arayat", "Santa Ana", "Mexico", "San Luis", "Candaba", "San Simon",
                "Apalit", "Macabebe", "Masantol", "Minalin", "Sasmuan", "Guagua",
                "Santa Rita", "Bacolor", "San Fernando", "Santo Tomas"
            ),
            MunicipalityProgression.sequence.map { it.sequenceName }
        )
        assertEquals(22, MunicipalityProgression.sequence.size)
        assertEquals("lubao", MunicipalityProgression.initialMunicipalityId)
        assertEquals("santo_tomas", MunicipalityProgression.finalMunicipalityId)
        assertEquals(
            LocalDataSource.municipalities.map { it.id }.toSet(),
            MunicipalityProgression.sequence.map { it.municipalityId }.toSet()
        )
    }

    @Test
    fun `each passed quiz unlocks only its immediate configured successor`() {
        MunicipalityProgression.sequence.dropLast(1).forEachIndexed { index, entry ->
            val unlocked = MunicipalityProgression.sequence
                .take(index + 1)
                .mapTo(mutableSetOf()) { it.municipalityId }
            val completed = MunicipalityProgression.sequence
                .take(index)
                .mapTo(mutableSetOf()) { it.municipalityId }
            val expectedSuccessor = MunicipalityProgression.sequence[index + 1].municipalityId

            val outcome = MunicipalityProgression.outcomeForPassedQuiz(
                municipalityId = entry.municipalityId,
                unlockedMunicipalityIds = unlocked,
                completedMunicipalityIds = completed
            )

            if (expectedSuccessor == MunicipalityProgression.finalMunicipalityId) {
                assertEquals(QuizProgressionOutcome.FinalMunicipalityUnlocked(expectedSuccessor), outcome)
            } else {
                assertEquals(QuizProgressionOutcome.NextMunicipalityUnlocked(expectedSuccessor), outcome)
            }
        }
    }

    @Test
    fun `passing Lubao unlocks only Floridablanca`() {
        val outcome = MunicipalityProgression.outcomeForPassedQuiz(
            municipalityId = "lubao",
            unlockedMunicipalityIds = setOf("lubao"),
            completedMunicipalityIds = emptySet()
        )

        assertEquals(
            QuizProgressionOutcome.NextMunicipalityUnlocked("floridablanca"),
            outcome
        )
    }

    @Test
    fun `locked and out of order quizzes are rejected`() {
        assertEquals(
            QuizAccess.Locked,
            MunicipalityProgression.accessFor("floridablanca", setOf("lubao"), emptySet())
        )
        assertEquals(
            QuizAccess.PrerequisiteIncomplete("lubao"),
            MunicipalityProgression.accessFor(
                "porac",
                setOf("lubao", "floridablanca", "porac"),
                emptySet()
            )
        )
    }

    @Test
    fun `retaking a completed quiz never unlocks another municipality`() {
        val outcome = MunicipalityProgression.outcomeForPassedQuiz(
            municipalityId = "lubao",
            unlockedMunicipalityIds = setOf("lubao", "floridablanca"),
            completedMunicipalityIds = setOf("lubao")
        )

        assertEquals(QuizProgressionOutcome.AlreadyCompleted, outcome)
    }

    @Test
    fun `an already unlocked successor does not skip ahead`() {
        assertEquals(
            QuizProgressionOutcome.NextMunicipalityAlreadyUnlocked("floridablanca"),
            MunicipalityProgression.outcomeForPassedQuiz(
                municipalityId = "lubao",
                unlockedMunicipalityIds = setOf("lubao", "floridablanca", "porac"),
                completedMunicipalityIds = emptySet()
            )
        )
    }

    @Test
    fun `unknown municipality has no next municipality`() {
        assertEquals(null, MunicipalityProgression.nextMunicipalityId("not_a_municipality"))
    }

    @Test
    fun `San Fernando unlocks Santo Tomas and Santo Tomas has no successor`() {
        val completedBeforeSanFernando = MunicipalityProgression.sequence
            .takeWhile { it.municipalityId != "san_fernando" }
            .mapTo(mutableSetOf()) { it.municipalityId }
        val unlockedBeforeSanFernando = completedBeforeSanFernando + "san_fernando"

        assertEquals(
            QuizProgressionOutcome.FinalMunicipalityUnlocked("santo_tomas"),
            MunicipalityProgression.outcomeForPassedQuiz(
                "san_fernando",
                unlockedBeforeSanFernando,
                completedBeforeSanFernando
            )
        )

        assertEquals(
            QuizProgressionOutcome.SequenceAlreadyComplete,
            MunicipalityProgression.outcomeForPassedQuiz(
                "santo_tomas",
                unlockedBeforeSanFernando + "santo_tomas",
                completedBeforeSanFernando + "san_fernando"
            )
        )
    }

    @Test
    fun `failed submission has no automatic progression outcome`() {
        assertTrue(QuizProgressionOutcome.QuizFailed is QuizProgressionOutcome)
    }
}
