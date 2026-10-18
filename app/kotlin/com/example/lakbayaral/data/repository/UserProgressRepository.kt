package com.example.lakbayaral.data.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.example.lakbayaral.data.local.ARProgressEntity
import com.example.lakbayaral.data.local.LOCAL_USER_ID
import com.example.lakbayaral.data.local.LakbayAralDao
import com.example.lakbayaral.data.local.LakbayAralDatabase
import com.example.lakbayaral.data.local.LeaderboardEntity
import com.example.lakbayaral.data.local.MunicipalityProgressEntity
import com.example.lakbayaral.data.local.QuizResultEntity
import com.example.lakbayaral.data.local.UserEntity
import com.example.lakbayaral.data.models.FirebaseARProgress
import com.example.lakbayaral.data.models.FirebaseMunicipalityProgress
import com.example.lakbayaral.data.models.FirebaseProgressSummary
import com.example.lakbayaral.data.models.FirebaseQuizProgress
import com.example.lakbayaral.data.models.FirebaseRemoteProgress
import com.example.lakbayaral.data.models.OnlineUserProfile
import com.example.lakbayaral.data.models.Player
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.models.QuizSubmission
import com.example.lakbayaral.data.models.UserProgress
import com.example.lakbayaral.data.progression.MunicipalityProgression
import com.example.lakbayaral.data.progression.QuizAccess
import com.example.lakbayaral.data.progression.QuizProgressionOutcome
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
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
    private val database: LakbayAralDatabase,
    private val dao: LakbayAralDao,
    private val firebaseRepo: FirebaseRepository
) {
    private val prefs = context.getSharedPreferences("lakbay_progress", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val activeUserId: String
        get() = prefs.getString(KEY_CURRENT_USER_ID, LOCAL_USER_ID) ?: LOCAL_USER_ID

    private val _progress = MutableStateFlow(UserProgress())
    private val firebaseSyncScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val firebaseSyncMutex = Mutex()

    /** A single observable source for access checks and map rendering. */
    val progress: UserProgress get() = _progress.value
    val progressUpdates: StateFlow<UserProgress> = _progress.asStateFlow()

    init {
        MunicipalityProgression.requireExactMunicipalityIds(
            LocalDataSource.municipalities.map { it.id }
        )
        initializeDatabase()
        updateProgress(loadProgress(activeUserId))
        saveLocalLeaderboardEntry()
    }

    fun isMunicipalityUnlocked(id: String): Boolean =
        MunicipalityProgression.contains(id) && progress.unlockedMunicipalities.contains(id)

    fun quizAccess(id: String): QuizAccess = MunicipalityProgression.accessFor(
        municipalityId = id,
        unlockedMunicipalityIds = progress.unlockedMunicipalities,
        completedMunicipalityIds = completedMunicipalityIds()
    )

    /**
     * Saves a result and, where permitted, its immediate next unlock in one
     * Room transaction. No caller is given an unlock id to select.
     */
    fun submitQuizAnswers(
        municipalityId: String,
        selectedAnswerIndexes: List<Int>
    ): QuizSubmission {
        val municipality = LocalDataSource.getMunicipality(municipalityId)
        if (municipality == null || !MunicipalityProgression.contains(municipalityId)) {
            return invalidQuizSubmission(municipalityId, 0, "Unknown municipality.")
        }

        val activeQuiz = if (firebaseRepo.isSignedIn()) {
            municipality.onlineQuiz.takeIf { it.isNotEmpty() } ?: municipality.quiz
        } else {
            municipality.quiz
        }
        if (activeQuiz.isEmpty()) {
            return invalidQuizSubmission(municipalityId, 0, "This municipality has no configured quiz.")
        }
        if (selectedAnswerIndexes.size != activeQuiz.size) {
            return invalidQuizSubmission(
                municipalityId,
                activeQuiz.size,
                "Quiz answers do not match the configured question count."
            )
        }
        if (activeQuiz.zip(selectedAnswerIndexes).any { (question, answerIndex) ->
                answerIndex !in question.choices.indices
            }
        ) {
            return invalidQuizSubmission(municipalityId, activeQuiz.size, "Invalid answer choice.")
        }

        // The UI supplies only answer indexes. Scoring and the pass decision are
        // recalculated here so an arbitrary score cannot invoke progression.
        val score = activeQuiz.zip(selectedAnswerIndexes).count { (question, answerIndex) ->
            question.correctIndex == answerIndex
        }
        val result = QuizResult(
            municipalityId = municipalityId,
            score = score,
            total = activeQuiz.size,
            passed = score * 100 / activeQuiz.size >= MunicipalityProgression.PASSING_PERCENTAGE
        )
        return QuizSubmission(result, persistQuizResult(result))
    }

    private fun persistQuizResult(result: QuizResult): QuizProgressionOutcome {
        validateQuizSubmission(result)?.let { reason ->
            return QuizProgressionOutcome.InvalidSubmission(reason)
        }

        val userId = activeUserId
        val outcome = writeBlocking {
            database.withTransaction {
                val municipalityProgress = dao.getMunicipalityProgress(userId)
                val unlockedIds = municipalityProgress
                    .filter { it.isUnlocked }
                    .mapTo(mutableSetOf()) { it.municipalityId }
                val completedIds = dao.getQuizResults(userId)
                    .filter { it.passed }
                    .mapTo(mutableSetOf()) { it.municipalityId }
                val access = MunicipalityProgression.accessFor(
                    municipalityId = result.municipalityId,
                    unlockedMunicipalityIds = unlockedIds,
                    completedMunicipalityIds = completedIds
                )

                if (access != QuizAccess.Allowed) {
                    return@withTransaction when (access) {
                        QuizAccess.Completed -> QuizProgressionOutcome.AlreadyCompleted
                        else -> QuizProgressionOutcome.Rejected(access)
                    }
                }

                val normalizedResult = result.copy(
                    score = result.score.coerceIn(0, result.total),
                    passed = result.score * 100 / result.total >= MunicipalityProgression.PASSING_PERCENTAGE
                )
                val existingResult = dao.getQuizResult(result.municipalityId, userId)
                val bestScore = maxOf(existingResult?.score ?: 0, normalizedResult.score)
                val scoreIncrease = (bestScore - (existingResult?.score ?: 0)).coerceAtLeast(0)
                val storedResult = normalizedResult.copy(
                    score = bestScore,
                    passed = (existingResult?.passed == true) || normalizedResult.passed
                )
                val shouldPersistResult = existingResult == null ||
                    bestScore > existingResult.score ||
                    storedResult.passed != existingResult.passed

                if (shouldPersistResult) {
                    dao.upsertQuizResult(storedResult.toEntity(userId))
                    val user = dao.getUser(userId) ?: UserEntity(userId = userId)
                    dao.upsertUser(
                        user.copy(
                            totalScore = user.totalScore + scoreIncrease * QUIZ_SCORE_MULTIPLIER,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }

                if (!normalizedResult.passed) {
                    return@withTransaction QuizProgressionOutcome.QuizFailed
                }

                when (
                    val progressionOutcome = MunicipalityProgression.outcomeForPassedQuiz(
                        municipalityId = result.municipalityId,
                        unlockedMunicipalityIds = unlockedIds,
                        completedMunicipalityIds = completedIds
                    )
                ) {
                    is QuizProgressionOutcome.NextMunicipalityUnlocked,
                    is QuizProgressionOutcome.FinalMunicipalityUnlocked -> {
                        dao.upsertMunicipalityProgress(
                            MunicipalityProgressEntity(
                                userId = userId,
                                municipalityId = progressionOutcome.unlockedMunicipalityId(),
                                isUnlocked = true,
                                updatedAt = System.currentTimeMillis()
                            )
                        )
                        progressionOutcome
                    }
                    else -> progressionOutcome
                }
            }
        }

        updateProgress(loadProgress(userId))
        saveLocalLeaderboardEntry()
        outcome.firebaseSyncTarget(result.municipalityId)?.let { target ->
            queueFirebaseSync(userId, target)
        }
        return outcome
    }

    fun saveARProgress(
        municipalityId: String,
        mode: String,
        placedCount: Int,
        totalCount: Int
    ) {
        if (!isMunicipalityUnlocked(municipalityId)) return
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
        queueFirebaseSync(userId, FirebaseSyncTarget.AR(municipalityId, mode))
    }

    fun getTotalScore(): Int = progress.totalScore

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
        // The menu switches between local profiles. Retaining a Firebase
        // session here could upload the newly selected local profile into the
        // previously authenticated account.
        firebaseRepo.signOut()
        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, user.userId)
            .putString("player_name", user.username)
            .apply()

        ensureMunicipalityProgress(user.userId)
        updateProgress(loadProgress(user.userId))
        saveLocalLeaderboardEntry()
        queueFirebaseSync(user.userId, FirebaseSyncTarget.All)
    }

    fun switchToUserName(name: String) {
        // A name-selected profile is a local/offline profile, not a rename of
        // the current Firebase account.
        firebaseRepo.signOut()
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
        updateProgress(loadProgress(userId))
        saveLocalLeaderboardEntry()
        queueFirebaseSync(userId, FirebaseSyncTarget.All)
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

        // "Delete User" in the local profile picker is not an online account
        // deletion operation. End the Firebase session so its replacement
        // local profile can never be synchronized into the deleted account.
        firebaseRepo.signOut()

        val activeUser = replacementUser ?: createEmptyReplacementUser(userId)
        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, activeUser.userId)
            .putString("player_name", activeUser.username)
            .apply()

        ensureMunicipalityProgress(activeUser.userId)
        updateProgress(loadProgress(activeUser.userId))
        saveLocalLeaderboardEntry()
        queueFirebaseSync(activeUser.userId, FirebaseSyncTarget.All)
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

    /** Syncs all locally stored progress for the active signed-in account. */
    suspend fun syncCurrentProgress(): Result<Unit> {
        val userId = activeUserId
        return firebaseSyncMutex.withLock {
            syncFirebaseProgress(userId, FirebaseSyncTarget.All)
        }
    }

    /**
     * Merges existing Firestore progress into Room before uploading again.
     * Existing unlocked municipalities and the higher local score are
     * preserved; Room primary keys make the operation idempotent. A brand-new
     * registration deliberately does not import a same-named guest profile:
     * new accounts must begin with Lubao only. Existing-account sign-in can
     * opt in to the one-time legacy import that predates Firebase-UID keyed
     * Room rows.
     */
    suspend fun restoreRemoteProgress(
        profile: OnlineUserProfile,
        importActiveLocalProfile: Boolean = true
    ): Result<Unit> {
        return firebaseSyncMutex.withLock {
            val remoteProgress = firebaseRepo.loadCurrentUserProgress()
                .getOrElse { error -> return@withLock Result.failure<Unit>(error) }
            val userId = withContext(Dispatchers.IO) {
                database.withTransaction {
                    mergeRemoteProgress(profile, remoteProgress, importActiveLocalProfile)
                }
            }

            updateProgress(loadProgress(userId))
            saveLocalLeaderboardEntry()
            syncFirebaseProgress(userId, FirebaseSyncTarget.All)
        }
    }

    private fun completedMunicipalityIds(): Set<String> =
        progress.quizResults
            .filterValues { it.passed }
            .keys

    /**
     * The UI supplies a completed quiz score, but the repository independently
     * verifies that it belongs to this municipality's configured local or
     * signed-in question set before it can affect progress.
     */
    private fun validateQuizSubmission(result: QuizResult): String? {
        val municipality = LocalDataSource.getMunicipality(result.municipalityId)
            ?: return "Unknown municipality."
        if (!MunicipalityProgression.contains(result.municipalityId)) {
            return "Municipality is not in the configured progression."
        }
        if (result.total <= 0 || result.score !in 0..result.total) {
            return "Invalid quiz score."
        }
        val activeQuiz = if (firebaseRepo.isSignedIn()) {
            municipality.onlineQuiz.takeIf { it.isNotEmpty() } ?: municipality.quiz
        } else {
            municipality.quiz
        }
        if (result.total != activeQuiz.size) {
            return "Quiz question count does not match the configured quiz."
        }
        return null
    }

    private fun invalidQuizSubmission(
        municipalityId: String,
        total: Int,
        reason: String
    ): QuizSubmission = QuizSubmission(
        result = QuizResult(
            municipalityId = municipalityId,
            score = 0,
            total = total,
            passed = false
        ),
        progressionOutcome = QuizProgressionOutcome.InvalidSubmission(reason)
    )

    private fun updateProgress(progress: UserProgress) {
        _progress.value = progress
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
                        isUnlocked = municipality.id == MunicipalityProgression.initialMunicipalityId ||
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
                .ifEmpty { mutableSetOf(MunicipalityProgression.initialMunicipalityId) }

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

    private fun queueFirebaseSync(userId: String, target: FirebaseSyncTarget) {
        // Guests remain local-only. Firestore's own offline cache queues writes for signed-in
        // users who temporarily lose connectivity.
        val signedInUid = firebaseRepo.currentUser?.uid ?: return

        firebaseSyncScope.launch {
            firebaseSyncMutex.withLock {
                // Never send a queued update from a previous account into a newly signed-in
                // account on the same device.
                if (firebaseRepo.currentUser?.uid == signedInUid) {
                    syncFirebaseProgress(userId, target).exceptionOrNull()?.let { error ->
                        Log.w(TAG, "Firestore progress sync failed for local user $userId.", error)
                    }
                }
            }
        }
    }

    private suspend fun mergeRemoteProgress(
        profile: OnlineUserProfile,
        remoteProgress: FirebaseRemoteProgress,
        importActiveLocalProfile: Boolean
    ): String {
        val cleanName = profile.username.trim().ifBlank { "Explorer" }
        val userId = firebaseLocalUserId(profile.uid)
        val existingUser = dao.getUser(userId)
        if (existingUser == null && importActiveLocalProfile) {
            migratePreviousFirebaseUidRow(profile, userId)
            if (dao.getUser(userId) == null) {
                migrateActiveLegacyUserIfMatching(profile, userId)
            }
        }
        val migratedUser = dao.getUser(userId)
        val remoteScore = remoteProgress.profile?.totalScore ?: 0
        dao.upsertUser(
            (migratedUser ?: UserEntity(userId = userId, username = cleanName)).copy(
                username = cleanName,
                totalScore = maxOf(migratedUser?.totalScore ?: 0, remoteScore),
                updatedAt = System.currentTimeMillis()
            )
        )
        ensureMunicipalityProgressLocked(userId)

        remoteProgress.municipalityProgress
            .filter { MunicipalityProgression.contains(it.municipalityId) }
            .forEach { remote ->
            val local = dao.getMunicipalityProgress(remote.municipalityId, userId)
            if (local == null || (remote.isUnlocked && !local.isUnlocked)) {
                dao.upsertMunicipalityProgress(
                    MunicipalityProgressEntity(
                        userId = userId,
                        municipalityId = remote.municipalityId,
                        isUnlocked = remote.isUnlocked || (local?.isUnlocked ?: false),
                        updatedAt = maxOf(local?.updatedAt ?: 0L, remote.localUpdatedAt)
                    )
                )
            }
        }

        val localQuizByMunicipality = dao.getQuizResults(userId)
            .associateBy { it.municipalityId }
        remoteProgress.quizProgress
            .filter { MunicipalityProgression.contains(it.municipalityId) }
            .forEach { remote ->
            val local = localQuizByMunicipality[remote.municipalityId]
            // Completion is sticky: a failed retry from another device must
            // never overwrite a legitimate passed result.
            if (local == null || remote.localUpdatedAt > local.updatedAt || (remote.passed && !local.passed)) {
                dao.upsertQuizResult(
                    QuizResultEntity(
                        userId = userId,
                        municipalityId = remote.municipalityId,
                        score = maxOf(local?.score ?: 0, remote.score),
                        total = maxOf(local?.total ?: 0, remote.total),
                        passed = (local?.passed ?: false) || remote.passed,
                        percentage = maxOf(local?.percentage ?: 0, remote.percentage),
                        updatedAt = maxOf(local?.updatedAt ?: 0L, remote.localUpdatedAt)
                    )
                )
            }
        }

        val localArByKey = dao.getARProgress(userId)
            .associateBy { "${it.municipalityId}__${it.mode}" }
        remoteProgress.arProgress.forEach { remote ->
            val local = localArByKey["${remote.municipalityId}__${remote.mode}"]
            dao.upsertARProgress(
                ARProgressEntity(
                    userId = userId,
                    municipalityId = remote.municipalityId,
                    mode = remote.mode,
                    placedCount = maxOf(local?.placedCount ?: 0, remote.placedCount),
                    totalCount = maxOf(local?.totalCount ?: 0, remote.totalCount),
                    completed = (local?.completed ?: false) || remote.completed,
                    updatedAt = maxOf(local?.updatedAt ?: 0L, remote.localUpdatedAt)
                )
            )
        }

        prefs.edit()
            .putString(KEY_CURRENT_USER_ID, userId)
            .putString("player_name", cleanName)
            .apply()
        return userId
    }

    /**
     * Earlier releases stored authenticated Room rows as `firebase_{uid}`.
     * That key is tied to the account rather than a display name, so it can be
     * imported safely even if the user has since opened another local profile.
     * The source rows remain intact as a recoverable legacy copy.
     */
    private suspend fun migratePreviousFirebaseUidRow(
        profile: OnlineUserProfile,
        firebaseUserId: String
    ) {
        val previousFirebaseUserId = "firebase_${profile.uid}"
        val previousUser = dao.getUser(previousFirebaseUserId) ?: return

        dao.upsertUser(
            previousUser.copy(
                userId = firebaseUserId,
                username = profile.username.trim().ifBlank { "Explorer" }
            )
        )
        dao.getMunicipalityProgress(previousFirebaseUserId).forEach { progress ->
            dao.upsertMunicipalityProgress(progress.copy(userId = firebaseUserId))
        }
        dao.getQuizResults(previousFirebaseUserId).forEach { result ->
            dao.upsertQuizResult(result.copy(userId = firebaseUserId))
        }
        dao.getARProgress(previousFirebaseUserId).forEach { progress ->
            dao.upsertARProgress(progress.copy(userId = firebaseUserId))
        }
    }

    /**
     * Older app versions used a username-derived Room key for signed-in users.
     * Copy only the currently active, matching legacy profile once so progress on
     * this device is retained without allowing another cached username to bleed
     * into a different Firebase account.
     */
    private suspend fun migrateActiveLegacyUserIfMatching(
        profile: OnlineUserProfile,
        firebaseUserId: String
    ) {
        val legacyUserId = activeUserId
        if (legacyUserId == firebaseUserId) return

        val legacyUser = dao.getUser(legacyUserId) ?: return
        if (!legacyUser.username.equals(profile.username, ignoreCase = true)) return

        dao.upsertUser(
            legacyUser.copy(
                userId = firebaseUserId,
                username = profile.username.trim().ifBlank { "Explorer" }
            )
        )
        dao.getMunicipalityProgress(legacyUserId).forEach { progress ->
            dao.upsertMunicipalityProgress(progress.copy(userId = firebaseUserId))
        }
        dao.getQuizResults(legacyUserId).forEach { result ->
            dao.upsertQuizResult(result.copy(userId = firebaseUserId))
        }
        dao.getARProgress(legacyUserId).forEach { progress ->
            dao.upsertARProgress(progress.copy(userId = firebaseUserId))
        }
    }

    private suspend fun syncFirebaseProgress(
        userId: String,
        target: FirebaseSyncTarget
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (!firebaseRepo.isSignedIn()) {
            return@withContext Result.failure<Unit>(
                IllegalStateException("Sign in online before syncing progress.")
            )
        }

        val user = dao.getUser(userId)
            ?: return@withContext Result.failure<Unit>(
                IllegalStateException("Local user was not found.")
            )
        val municipalityProgress = dao.getMunicipalityProgress(userId)
        val quizResults = dao.getQuizResults(userId)
        val arProgress = dao.getARProgress(userId)
        val unlockedIds = municipalityProgress
            .asSequence()
            .filter { it.isUnlocked }
            .map { it.municipalityId }
            .toSet()
        val unlockedMunicipalityIds = MunicipalityProgression.sequence
            .map { it.municipalityId }
            .filter { it in unlockedIds }
        val quizAccuracy = if (quizResults.isNotEmpty()) {
            quizResults.sumOf { it.percentage }.toFloat() / quizResults.size
        } else {
            0f
        }
        val summary = FirebaseProgressSummary(
            playerName = user.username,
            totalScore = user.totalScore,
            municipalitiesUnlocked = unlockedMunicipalityIds.size,
            unlockedMunicipalityIds = unlockedMunicipalityIds,
            quizAccuracy = quizAccuracy
        )

        firebaseRepo.syncProgress(
            summary = summary,
            // Unlock and passed-completion records are monotonic. Uploading
            // local false/failed rows would let an offline, stale device erase
            // legitimate remote progress from another device.
            municipalityProgress = target.municipalityProgress(municipalityProgress)
                .filter { it.isUnlocked },
            quizProgress = target.quizProgress(quizResults)
                .filter { it.passed },
            arProgress = target.arProgress(arProgress)
        )
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

    private fun firebaseLocalUserId(uid: String): String {
        require(uid.isNotBlank()) { "Firebase user id is required." }
        // Room progress for online players uses the Firebase Authentication UID
        // itself. This avoids name-derived identity and makes the local and
        // Firestore ownership keys directly traceable to the same account.
        return uid
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

    private fun QuizProgressionOutcome.unlockedMunicipalityId(): String = when (this) {
        is QuizProgressionOutcome.NextMunicipalityUnlocked -> municipalityId
        is QuizProgressionOutcome.FinalMunicipalityUnlocked -> municipalityId
        else -> error("This outcome does not unlock a municipality.")
    }

    private fun QuizProgressionOutcome.firebaseSyncTarget(
        municipalityId: String
    ): FirebaseSyncTarget? = when (this) {
        QuizProgressionOutcome.QuizFailed,
        QuizProgressionOutcome.SequenceAlreadyComplete,
        is QuizProgressionOutcome.NextMunicipalityAlreadyUnlocked,
        is QuizProgressionOutcome.FinalMunicipalityAlreadyUnlocked ->
            FirebaseSyncTarget.QuizCompletion(municipalityId, newlyUnlockedMunicipalityId = null)
        is QuizProgressionOutcome.NextMunicipalityUnlocked,
        is QuizProgressionOutcome.FinalMunicipalityUnlocked ->
            FirebaseSyncTarget.QuizCompletion(
                municipalityId = municipalityId,
                newlyUnlockedMunicipalityId = unlockedMunicipalityId()
            )
        QuizProgressionOutcome.AlreadyCompleted,
        is QuizProgressionOutcome.Rejected,
        is QuizProgressionOutcome.InvalidSubmission -> null
    }

    private sealed interface FirebaseSyncTarget {
        data object All : FirebaseSyncTarget
        data class QuizCompletion(
            val municipalityId: String,
            val newlyUnlockedMunicipalityId: String?
        ) : FirebaseSyncTarget
        data class AR(val municipalityId: String, val mode: String) : FirebaseSyncTarget

        fun municipalityProgress(
            values: List<MunicipalityProgressEntity>
        ): List<FirebaseMunicipalityProgress> = when (this) {
            All -> values.map { value ->
                FirebaseMunicipalityProgress(
                    municipalityId = value.municipalityId,
                    isUnlocked = value.isUnlocked,
                    localUpdatedAt = value.updatedAt
                )
            }
            is QuizCompletion -> values
                .filter { it.municipalityId == newlyUnlockedMunicipalityId }
                .map { value ->
                    FirebaseMunicipalityProgress(
                        municipalityId = value.municipalityId,
                        isUnlocked = value.isUnlocked,
                        localUpdatedAt = value.updatedAt
                    )
                }
            else -> emptyList()
        }

        fun quizProgress(values: List<QuizResultEntity>): List<FirebaseQuizProgress> = when (this) {
            All -> values.map { value ->
                FirebaseQuizProgress(
                    municipalityId = value.municipalityId,
                    score = value.score,
                    total = value.total,
                    passed = value.passed,
                    percentage = value.percentage,
                    localUpdatedAt = value.updatedAt
                )
            }
            is QuizCompletion -> values
                .filter { it.municipalityId == municipalityId }
                .map { value ->
                    FirebaseQuizProgress(
                        municipalityId = value.municipalityId,
                        score = value.score,
                        total = value.total,
                        passed = value.passed,
                        percentage = value.percentage,
                        localUpdatedAt = value.updatedAt
                    )
                }
            else -> emptyList()
        }

        fun arProgress(values: List<ARProgressEntity>): List<FirebaseARProgress> = when (this) {
            All -> values.map { value ->
                FirebaseARProgress(
                    municipalityId = value.municipalityId,
                    mode = value.mode,
                    placedCount = value.placedCount,
                    totalCount = value.totalCount,
                    completed = value.completed,
                    localUpdatedAt = value.updatedAt
                )
            }
            is AR -> values
                .filter { it.municipalityId == municipalityId && it.mode == mode }
                .map { value ->
                    FirebaseARProgress(
                        municipalityId = value.municipalityId,
                        mode = value.mode,
                        placedCount = value.placedCount,
                        totalCount = value.totalCount,
                        completed = value.completed,
                        localUpdatedAt = value.updatedAt
                    )
                }
            else -> emptyList()
        }
    }

    private companion object {
        const val KEY_CURRENT_USER_ID = "current_user_id"
        const val TAG = "UserProgressRepository"
        const val QUIZ_SCORE_MULTIPLIER = 10
    }
}
