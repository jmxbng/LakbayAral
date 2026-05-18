package com.example.lakbayaral.data.repository

import android.content.Context
import com.example.lakbayaral.data.local.ARProgressEntity
import com.example.lakbayaral.data.local.LOCAL_USER_ID
import com.example.lakbayaral.data.local.LakbayAralDao
import com.example.lakbayaral.data.local.LeaderboardEntity
import com.example.lakbayaral.data.local.MunicipalityProgressEntity
import com.example.lakbayaral.data.local.QuizResultEntity
import com.example.lakbayaral.data.local.UserEntity
import com.example.lakbayaral.data.models.Player
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.models.UserProgress
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: LakbayAralDao
) {
    private val prefs = context.getSharedPreferences("lakbay_progress", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var _progress = UserProgress()

    val progress: UserProgress get() = _progress

    init {
        initializeDatabase()
        _progress = loadProgress()
        saveLocalLeaderboardEntry()
    }

    fun unlockMunicipality(id: String) {
        _progress.unlockedMunicipalities.add(id)
        writeBlocking {
            dao.upsertMunicipalityProgress(
                MunicipalityProgressEntity(
                    municipalityId = id,
                    isUnlocked = true,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        saveLocalLeaderboardEntry()
    }

    fun isMunicipalityUnlocked(id: String): Boolean =
        _progress.unlockedMunicipalities.contains(id)

    fun saveQuizResult(result: QuizResult) {
        _progress.quizResults[result.municipalityId] = result
        val newScore = _progress.totalScore + result.score * 10
        _progress = _progress.copy(totalScore = newScore)

        writeBlocking {
            dao.upsertQuizResult(result.toEntity())
            val user = dao.getUser() ?: UserEntity()
            dao.upsertUser(user.copy(totalScore = newScore, updatedAt = System.currentTimeMillis()))
        }

        if (result.passed) unlockNextMunicipality(result.municipalityId)
        saveLocalLeaderboardEntry()
    }

    fun saveARProgress(
        municipalityId: String,
        mode: String,
        placedCount: Int,
        totalCount: Int
    ) {
        writeBlocking {
            dao.upsertARProgress(
                ARProgressEntity(
                    municipalityId = municipalityId,
                    mode = mode,
                    placedCount = placedCount,
                    totalCount = totalCount,
                    completed = totalCount > 0 && placedCount >= totalCount
                )
            )
        }
    }

    fun getTotalScore(): Int = _progress.totalScore

    fun getPlayerName(): String = readBlocking {
        dao.getUser()?.username ?: "Explorer"
    }

    fun setPlayerName(name: String) {
        val cleanName = name.ifBlank { "Explorer" }
        writeBlocking {
            val user = dao.getUser() ?: UserEntity()
            dao.upsertUser(user.copy(username = cleanName, updatedAt = System.currentTimeMillis()))
        }
        saveLocalLeaderboardEntry()
    }

    fun saveLocalLeaderboardEntry() {
        writeBlocking {
            val user = dao.getUser() ?: UserEntity()
            val results = dao.getQuizResults()
            val accuracy = if (results.isNotEmpty()) {
                results.sumOf { it.percentage }.toFloat() / results.size
            } else {
                0f
            }

            dao.upsertLeaderboardEntry(
                LeaderboardEntity(
                    userId = LOCAL_USER_ID,
                    displayName = user.username,
                    totalScore = user.totalScore,
                    municipalitiesUnlocked = _progress.unlockedMunicipalities.size,
                    quizAccuracy = accuracy
                )
            )
        }
    }

    fun getLeaderboardPlayers(limit: Int = 10): List<Player> {
        saveLocalLeaderboardEntry()
        return readBlocking {
            dao.getLeaderboard(limit).mapIndexed { index, entry ->
                Player(
                    uid = entry.userId,
                    displayName = entry.displayName,
                    totalScore = entry.totalScore,
                    municipalitiesUnlocked = entry.municipalitiesUnlocked,
                    quizAccuracy = entry.quizAccuracy,
                    rank = index + 1
                )
            }
        }
    }

    private fun unlockNextMunicipality(currentId: String) {
        val municipalities = LocalDataSource.municipalities
        val currentIndex = municipalities.indexOfFirst { it.id == currentId }
        if (currentIndex >= 0 && currentIndex + 1 < municipalities.size) {
            val nextId = municipalities[currentIndex + 1].id
            unlockMunicipality(nextId)
        }
    }

    private fun initializeDatabase() {
        val legacyProgress = loadLegacyProgress()
        val legacyName = prefs.getString("player_name", "Explorer") ?: "Explorer"
        val shouldMigrate = !prefs.getBoolean("room_migrated", false)

        writeBlocking {
            val existingUser = dao.getUser()
            if (existingUser == null) {
                dao.upsertUser(
                    UserEntity(
                        username = legacyName,
                        totalScore = if (shouldMigrate) legacyProgress.totalScore else 0
                    )
                )
            }

            LocalDataSource.municipalities.forEach { municipality ->
                val existing = dao.getMunicipalityProgress(municipality.id)
                if (existing == null) {
                    dao.upsertMunicipalityProgress(
                        MunicipalityProgressEntity(
                            municipalityId = municipality.id,
                            isUnlocked = municipality.isUnlocked ||
                                (shouldMigrate && legacyProgress.unlockedMunicipalities.contains(municipality.id))
                        )
                    )
                }
            }

            if (shouldMigrate) {
                legacyProgress.quizResults.values.forEach { result ->
                    dao.upsertQuizResult(result.toEntity())
                }
            }
        }

        if (shouldMigrate) {
            prefs.edit().putBoolean("room_migrated", true).apply()
        }
    }

    private fun loadProgress(): UserProgress {
        return readBlocking {
            val unlocked = dao.getMunicipalityProgress()
                .filter { it.isUnlocked }
                .map { it.municipalityId }
                .toMutableSet()
                .ifEmpty { mutableSetOf("lubao") }

            val quizResults = dao.getQuizResults()
                .associate { entity -> entity.municipalityId to entity.toModel() }
                .toMutableMap()

            UserProgress(
                unlockedMunicipalities = unlocked,
                totalScore = dao.getUser()?.totalScore ?: 0,
                quizResults = quizResults
            )
        }
    }

    private fun loadLegacyProgress(): UserProgress {
        val json = prefs.getString("progress_json", null) ?: return UserProgress()
        return try {
            gson.fromJson(json, UserProgress::class.java) ?: UserProgress()
        } catch (e: Exception) {
            UserProgress()
        }
    }

    private fun QuizResult.toEntity(): QuizResultEntity =
        QuizResultEntity(
            municipalityId = municipalityId,
            score = score,
            total = total,
            passed = passed,
            percentage = percentage
        )

    private fun QuizResultEntity.toModel(): QuizResult =
        QuizResult(
            municipalityId = municipalityId,
            score = score,
            total = total,
            passed = passed
        )

    private fun <T> readBlocking(block: suspend () -> T): T =
        runBlocking(Dispatchers.IO) { block() }

    private fun <T> writeBlocking(block: suspend () -> T): T =
        runBlocking(Dispatchers.IO) { block() }
}
