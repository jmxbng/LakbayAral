package com.example.lakbayaral.data.repository

import android.content.Context
import com.example.lakbayaral.data.local.ARProgressEntity
import com.example.lakbayaral.data.local.LOCAL_USER_ID
import com.example.lakbayaral.data.local.LakbayAralDao
import com.example.lakbayaral.data.local.LeaderboardEntity
import com.example.lakbayaral.data.local.MunicipalityProgressEntity
import com.example.lakbayaral.data.local.QuizResultEntity
import com.example.lakbayaral.data.local.UserEntity
import com.example.lakbayaral.data.models.Municipality
import com.example.lakbayaral.data.models.Player
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.models.UserProgress
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class LocalUserProfile(
    val userId: String,
    val username: String,
    val totalScore: Int
)

@Singleton
class UserProgressRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: LakbayAralDao
) {
    private val prefs = context.getSharedPreferences("lakbay_progress", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val activeUserId: String
        get() = prefs.getString(KEY_CURRENT_USER_ID, LOCAL_USER_ID) ?: LOCAL_USER_ID

    private var _progress = UserProgress()

    val progress: UserProgress get() = _progress

    init {
        initializeDatabase()
        _progress = loadProgress(activeUserId)
        saveLocalLeaderboardEntry()
    }

    fun unlockMunicipality(id: String) {
        val userId = activeUserId
        _progress.unlockedMunicipalities.add(id)
        writeBlocking {
            dao.upsertMunicipalityProgress(
                MunicipalityProgressEntity(
                    userId = userId,
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
        val userId = activeUserId
        _progress.quizResults[result.municipalityId] = result
        val newScore = _progress.totalScore + result.score * 10
        _progress = _progress.copy(totalScore = newScore)

        writeBlocking {
            dao.upsertQuizResult(result.toEntity(userId))
            val user = dao.getUser(userId) ?: UserEntity(userId = userId)
            dao.upsertUser(user.copy(totalScore = newScore, updatedAt = System.currentTimeMillis()))
        }

        saveLocalLeaderboardEntry()
    }

    fun getMunicipalityUnlockChoices(completedMunicipalityId: String): List<Municipality> =
        LocalDataSource.municipalities.filter { municipality ->
            municipality.id != completedMunicipalityId &&
                !isMunicipalityUnlocked(municipality.id)
        }

    fun saveARProgress(
        municipalityId: String,
        mode: String,
        placedCount: Int,
        totalCount: Int
    ) {
        val userId = activeUserId
        writeBlocking {
            dao.upsertARProgress(
                ARProgressEntity(
                    userId = userId,
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
        dao.getUser(activeUserId)?.username ?: "Explorer"
    }

    fun setPlayerName(name: String) {
        switchToUserName(name)
    }

    fun getCurrentUserId(): String = activeUserId

    fun getLocalUsers(): List<LocalUserProfile> = readBlocking {
        dao.getUsers().map { user ->
            LocalUserProfile(
                userId = user.userId,
                username = user.username,
                totalScore = user.totalScore
            )
        }
    }

    fun switchToUser(userId: String) {
        val user = readBlocking { dao.getUser(userId) } ?: return
        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, user.userId)
            .putString("player_name", user.username)
            .apply()

        ensureMunicipalityProgress(user.userId)
        _progress = loadProgress(user.userId)
        saveLocalLeaderboardEntry()
    }

    fun switchToUserName(name: String) {
        val cleanName = name.trim().ifBlank { "Explorer" }
        val userId = readBlocking {
            dao.getUserByUsername(cleanName)?.userId ?: createLocalUserId(cleanName)
        }

        writeBlocking {
            val existing = dao.getUser(userId)
            val user = existing ?: UserEntity(userId = userId, username = cleanName)
            dao.upsertUser(user.copy(username = cleanName, updatedAt = System.currentTimeMillis()))
        }

        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, userId)
            .putString("player_name", cleanName)
            .apply()

        ensureMunicipalityProgress(userId)
        _progress = loadProgress(userId)
        saveLocalLeaderboardEntry()
    }

    fun deleteUser(userId: String) {
        val deletingActiveUser = userId == activeUserId
        var replacementUser: UserEntity? = null
        var deleted = false

        writeBlocking {
            if (dao.getUser(userId) != null) {
                dao.deleteMunicipalityProgress(userId)
                dao.deleteARProgress(userId)
                dao.deleteQuizResults(userId)
                dao.deleteLeaderboardEntry(userId)
                dao.deleteUser(userId)
                deleted = true

                if (deletingActiveUser) {
                    replacementUser = dao.getUsers().firstOrNull()
                }
            }
        }

        if (!deleted || !deletingActiveUser) return

        val activeUser = replacementUser ?: createEmptyReplacementUser(userId)
        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, activeUser.userId)
            .putString("player_name", activeUser.username)
            .apply()

        ensureMunicipalityProgress(activeUser.userId)
        _progress = loadProgress(activeUser.userId)
        saveLocalLeaderboardEntry()
    }

    fun saveLocalLeaderboardEntry() {
        val userId = activeUserId
        writeBlocking {
            val user = dao.getUser(userId) ?: UserEntity(userId = userId)
            val results = dao.getQuizResults(userId)
            val unlockedCount = dao.getMunicipalityProgress(userId).count { it.isUnlocked }
            val accuracy = if (results.isNotEmpty()) {
                results.sumOf { it.percentage }.toFloat() / results.size
            } else {
                0f
            }

            dao.upsertLeaderboardEntry(
                LeaderboardEntity(
                    userId = userId,
                    displayName = user.username,
                    totalScore = user.totalScore,
                    municipalitiesUnlocked = unlockedCount,
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

    private fun initializeDatabase() {
        val userId = activeUserId
        val legacyProgress = loadLegacyProgress()
        val legacyName = prefs.getString("player_name", "Explorer") ?: "Explorer"
        val shouldMigrate = !prefs.getBoolean("room_migrated", false)

        writeBlocking {
            val existingUser = dao.getUser(userId)
            if (existingUser == null) {
                dao.upsertUser(
                    UserEntity(
                        userId = userId,
                        username = legacyName,
                        totalScore = if (shouldMigrate) legacyProgress.totalScore else 0
                    )
                )
            }

            ensureMunicipalityProgressLocked(userId, legacyProgress, shouldMigrate)

            if (shouldMigrate) {
                legacyProgress.quizResults.values.forEach { result ->
                    dao.upsertQuizResult(result.toEntity(userId))
                }
            }
        }

        if (shouldMigrate) {
            prefs.edit().putBoolean("room_migrated", true).apply()
        }
    }

    private fun ensureMunicipalityProgress(userId: String) {
        writeBlocking {
            ensureMunicipalityProgressLocked(userId)
        }
    }

    private suspend fun ensureMunicipalityProgressLocked(
        userId: String,
        legacyProgress: UserProgress = UserProgress(),
        shouldMigrate: Boolean = false
    ) {
        LocalDataSource.municipalities.forEach { municipality ->
            val existing = dao.getMunicipalityProgress(municipality.id, userId)
            if (existing == null) {
                dao.upsertMunicipalityProgress(
                    MunicipalityProgressEntity(
                        userId = userId,
                        municipalityId = municipality.id,
                        isUnlocked = municipality.isUnlocked ||
                            (shouldMigrate && legacyProgress.unlockedMunicipalities.contains(municipality.id))
                    )
                )
            }
        }
    }

    private fun loadProgress(userId: String): UserProgress {
        return readBlocking {
            val unlocked = dao.getMunicipalityProgress(userId)
                .filter { it.isUnlocked }
                .map { it.municipalityId }
                .toMutableSet()
                .ifEmpty { mutableSetOf("lubao") }

            val quizResults = dao.getQuizResults(userId)
                .associate { entity -> entity.municipalityId to entity.toModel() }
                .toMutableMap()

            UserProgress(
                unlockedMunicipalities = unlocked,
                totalScore = dao.getUser(userId)?.totalScore ?: 0,
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

    private fun QuizResult.toEntity(userId: String): QuizResultEntity =
        QuizResultEntity(
            userId = userId,
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

    private fun createLocalUserId(name: String): String {
        val normalizedName = name.trim().lowercase(Locale.ROOT)
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(normalizedName.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
            .take(16)
        return "local_$digest"
    }

    private fun createEmptyReplacementUser(deletedUserId: String): UserEntity {
        val replacementUserId = if (deletedUserId == LOCAL_USER_ID) {
            "local_${System.currentTimeMillis()}"
        } else {
            LOCAL_USER_ID
        }
        val replacementUser = UserEntity(userId = replacementUserId, username = "Explorer")
        writeBlocking {
            dao.upsertUser(replacementUser)
        }
        return replacementUser
    }

    private fun <T> readBlocking(block: suspend () -> T): T =
        runBlocking(Dispatchers.IO) { block() }

    private fun <T> writeBlocking(block: suspend () -> T): T =
        runBlocking(Dispatchers.IO) { block() }

    private companion object {
        const val KEY_CURRENT_USER_ID = "current_user_id"
    }
}
