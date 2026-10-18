package com.example.lakbayaral.data.models

import com.example.lakbayaral.data.progression.MunicipalityProgression
import com.example.lakbayaral.data.progression.QuizProgressionOutcome

// ── Municipality ──────────────────────────────────────────────────────────────
data class Municipality(
    val id          : String,
    val name        : String,
    val province    : String,
    val description : String,
    val imageRes    : String,          // drawable resource name
    val isUnlocked  : Boolean = false,
    val historicalPlaces : List<HistoricalObject> = emptyList(),
    val historicalPeople : List<HistoricalObject> = emptyList(),
    val quiz        : List<QuizQuestion> = emptyList(),
    val onlineQuiz  : List<QuizQuestion> = emptyList()
)

// ── Historical AR Object ──────────────────────────────────────────────────────
data class HistoricalObject(
    val id          : String,
    val name        : String,
    val type        : ObjectType,
    val description : String,
    val significance: String,
    val yearBuilt   : String = "",
    val modelAsset  : String = "",      // path inside assets/models/
    val thumbnailRes: String = ""
)

enum class ObjectType { PLACE, PERSON }

// ── Quiz ──────────────────────────────────────────────────────────────────────
data class QuizQuestion(
    val id            : String,
    val question      : String,
    val choices       : List<String>,
    val correctIndex  : Int,
    val explanation   : String = ""
)

data class QuizResult(
    val municipalityId : String,
    val score          : Int,
    val total          : Int,
    val passed         : Boolean
) {
    val percentage: Int get() = if (total > 0) (score * 100 / total) else 0
}

/**
 * The repository calculates the score from the submitted answer indexes, then
 * returns both the stored quiz result and the only progression outcome allowed
 * by the configured municipality sequence.
 */
data class QuizSubmission(
    val result: QuizResult,
    val progressionOutcome: QuizProgressionOutcome
)

// ── Player / Leaderboard ──────────────────────────────────────────────────────
data class Player(
    val uid                  : String = "",
    val displayName          : String = "Anonymous",
    val totalScore           : Int    = 0,
    val municipalitiesUnlocked: Int   = 1,
    val quizAccuracy         : Float  = 0f,
    val rank                 : Int    = 0,
    val pvpPoints            : Int    = 0,
    val onlineWins           : Int    = 0,
    val onlineMatches        : Int    = 0
)

data class OnlineUserProfile(
    val uid: String = "",
    val username: String = "Explorer",
    val email: String = "",
    val totalScore: Int = 0,
    val pvpPoints: Int = 0,
    val municipalitiesUnlocked: Int = 1,
    // Legacy/bootstrap summary only. Per-municipality Firestore records are
    // authoritative and this summary is deliberately immutable after profile
    // creation so it cannot become an alternate client-side unlock channel.
    val unlockedMunicipalityIds: List<String> = listOf(MunicipalityProgression.initialMunicipalityId),
    val quizAccuracy: Float = 0f,
    val onlineWins: Int = 0,
    val onlineMatches: Int = 0
)

// ── Cloud progress sync ──────────────────────────────────────────────────────
data class FirebaseProgressSummary(
    val playerName: String,
    val totalScore: Int,
    val municipalitiesUnlocked: Int,
    val unlockedMunicipalityIds: List<String>,
    val quizAccuracy: Float
)

data class FirebaseMunicipalityProgress(
    val municipalityId: String,
    val isUnlocked: Boolean,
    val localUpdatedAt: Long
)

data class FirebaseQuizProgress(
    val municipalityId: String,
    val score: Int,
    val total: Int,
    val passed: Boolean,
    val percentage: Int,
    val localUpdatedAt: Long
)

data class FirebaseARProgress(
    val municipalityId: String,
    val mode: String,
    val placedCount: Int,
    val totalCount: Int,
    val completed: Boolean,
    val localUpdatedAt: Long
)

/** The existing Firestore records for one authenticated user. */
data class FirebaseRemoteProgress(
    val profile: OnlineUserProfile?,
    val municipalityProgress: List<FirebaseMunicipalityProgress>,
    val quizProgress: List<FirebaseQuizProgress>,
    val arProgress: List<FirebaseARProgress>
)

// ── User Progress (local) ─────────────────────────────────────────────────────
data class UserProgress(
    val unlockedMunicipalities : MutableSet<String> = mutableSetOf(MunicipalityProgression.initialMunicipalityId),
    val totalScore             : Int = 0,
    val quizResults            : MutableMap<String, QuizResult> = mutableMapOf()
)
