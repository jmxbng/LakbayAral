package com.example.lakbayaral.data.models

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
    val quiz        : List<QuizQuestion> = emptyList()
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
    val quizAccuracy: Float = 0f,
    val onlineWins: Int = 0,
    val onlineMatches: Int = 0
)

// ── User Progress (local) ─────────────────────────────────────────────────────
data class UserProgress(
    val unlockedMunicipalities : MutableSet<String> = mutableSetOf("lubao"),
    val totalScore             : Int = 0,
    val quizResults            : MutableMap<String, QuizResult> = mutableMapOf()
)
