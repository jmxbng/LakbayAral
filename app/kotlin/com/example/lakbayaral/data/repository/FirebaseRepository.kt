package com.example.lakbayaral.data.repository

import com.example.lakbayaral.data.models.Player
import com.example.lakbayaral.data.models.FirebaseARProgress
import com.example.lakbayaral.data.models.FirebaseMunicipalityProgress
import com.example.lakbayaral.data.models.FirebaseProgressSummary
import com.example.lakbayaral.data.models.FirebaseQuizProgress
import com.example.lakbayaral.data.models.FirebaseRemoteProgress
import com.example.lakbayaral.data.models.OnlineUserProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val usersRef = firestore.collection("online_users")
    private val leaderboardRef = firestore.collection("online_leaderboard")

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun isSignedIn(): Boolean = currentUser != null

    suspend fun signInAnonymously(): String? {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registerWithEmail(username: String, email: String, password: String): Result<OnlineUserProfile> =
        runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: error("Account created without a user id.")
            requireCurrentUserOwns(uid)
            val profile = OnlineUserProfile(
                uid = uid,
                username = username.trim().ifBlank { "Explorer" },
                email = currentUser?.email ?: email.trim()
            )
            saveProfile(profile)
            getProfile(uid) ?: error("Profile was not found after it was created.")
        }

    suspend fun signInWithEmail(email: String, password: String): Result<OnlineUserProfile> =
        runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: error("Signed in without a user id.")
            requireCurrentUserOwns(uid)
            getProfile(uid) ?: run {
                // A Firebase Auth account can legitimately outlive a deleted
                // profile. Re-create only its own missing profile; never merge
                // into or overwrite a document at another path.
                val newProfile = OnlineUserProfile(
                    uid = uid,
                    username = email.substringBefore("@").ifBlank { "Explorer" },
                    email = currentUser?.email ?: email.trim()
                )
                saveProfile(newProfile)
                getProfile(uid) ?: error("Profile was not found after it was created.")
            }
        }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun getProfile(uid: String = currentUser?.uid ?: ""): OnlineUserProfile? {
        if (uid.isBlank()) return null
        requireCurrentUserOwns(uid)
        return usersRef.document(uid).get().await().toOnlineUserProfile(uid)
    }

    /**
     * Reads the existing progress under the current user's established Firestore paths.
     * It does not create, delete, or rename any cloud documents.
     */
    suspend fun loadCurrentUserProgress(): Result<FirebaseRemoteProgress> = runCatching {
        val uid = currentUser?.uid ?: error("Sign in online before loading progress.")
        val userDocument = usersRef.document(uid)
        val profile = userDocument.get().await().toOnlineUserProfile(uid)

        val municipalityProgress = userDocument.collection("municipality_progress")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val municipalityId = document.getString("municipalityId") ?: document.id
                FirebaseMunicipalityProgress(
                    municipalityId = municipalityId,
                    isUnlocked = document.getBoolean("isUnlocked") ?: false,
                    localUpdatedAt = document.getLong("localUpdatedAt") ?: 0L
                )
            }
            .toMutableMapByMunicipalityId()
            .also { progressByMunicipality ->
                // Profiles written before per-municipality records existed used
                // this summary as their only progress source. Once even one
                // per-municipality record exists, those records are
                // authoritative: accepting a later stale summary would let it
                // recreate a progress row that another device intentionally
                // does not have.
                if (progressByMunicipality.isEmpty()) {
                    profile?.unlockedMunicipalityIds.orEmpty().forEach { municipalityId ->
                        progressByMunicipality[municipalityId] = FirebaseMunicipalityProgress(
                            municipalityId = municipalityId,
                            isUnlocked = true,
                            localUpdatedAt = 0L
                        )
                    }
                }
            }
            .values
            .toList()

        val quizProgress = userDocument.collection("quiz_results")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val municipalityId = document.getString("municipalityId") ?: return@mapNotNull null
                FirebaseQuizProgress(
                    municipalityId = municipalityId,
                    score = (document.getLong("score") ?: 0L).toInt(),
                    total = (document.getLong("total") ?: 0L).toInt(),
                    passed = document.getBoolean("passed") ?: false,
                    percentage = (document.getLong("percentage") ?: 0L).toInt(),
                    localUpdatedAt = document.getLong("localUpdatedAt") ?: 0L
                )
            }

        val arProgress = userDocument.collection("ar_progress")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                val municipalityId = document.getString("municipalityId") ?: return@mapNotNull null
                val mode = document.getString("mode") ?: return@mapNotNull null
                FirebaseARProgress(
                    municipalityId = municipalityId,
                    mode = mode,
                    placedCount = (document.getLong("placedCount") ?: 0L).toInt(),
                    totalCount = (document.getLong("totalCount") ?: 0L).toInt(),
                    completed = document.getBoolean("completed") ?: false,
                    localUpdatedAt = document.getLong("localUpdatedAt") ?: 0L
                )
            }

        FirebaseRemoteProgress(
            profile = profile,
            municipalityProgress = municipalityProgress,
            quizProgress = quizProgress,
            arProgress = arProgress
        )
    }

    suspend fun saveProfile(profile: OnlineUserProfile) {
        requireCurrentUserOwns(profile.uid)
        usersRef.document(profile.uid).set(profileData(profile), SetOptions.merge()).await()
    }

    /**
     * Updates the signed-in user's profile and leaderboard summary, then writes only the
     * progress records that changed. This keeps Firestore writes small while ensuring a
     * municipality unlock, quiz result, or AR placement is persisted immediately.
     */
    suspend fun syncProgress(
        summary: FirebaseProgressSummary,
        municipalityProgress: List<FirebaseMunicipalityProgress> = emptyList(),
        quizProgress: List<FirebaseQuizProgress> = emptyList(),
        arProgress: List<FirebaseARProgress> = emptyList()
    ): Result<Unit> = runCatching {
        val uid = currentUser?.uid ?: error("Sign in online before syncing progress.")
        val syncedMunicipalityIds = summary.unlockedMunicipalityIds.distinct().sorted()
        // These ids are derived from the authoritative per-municipality rows.
        // Profile bootstrap fields stay immutable; the leaderboard receives
        // the current derived count for display.
        val syncedMunicipalities = syncedMunicipalityIds.size
        val profile = OnlineUserProfile(
            uid = uid,
            username = summary.playerName.trim().ifBlank { "Explorer" },
            email = currentUser?.email.orEmpty(),
            totalScore = summary.totalScore,
            pvpPoints = calculatePvpPoints(
                totalScore = summary.totalScore,
                municipalitiesCompleted = syncedMunicipalities,
                accuracy = summary.quizAccuracy
            ),
            municipalitiesUnlocked = syncedMunicipalities,
            unlockedMunicipalityIds = syncedMunicipalityIds,
            quizAccuracy = summary.quizAccuracy
        )

        val batch = firestore.batch()
        batch.set(
            usersRef.document(uid),
            progressProfileData(profile) + mapOf(
                "progressUpdatedAt" to Timestamp.now()
            ),
            SetOptions.merge()
        )
        batch.set(
            leaderboardRef.document(uid),
            leaderboardProgressData(profile),
            SetOptions.merge()
        )

        municipalityProgress.forEach { progress ->
            batch.set(
                usersRef.document(uid).collection("municipality_progress").document(progress.municipalityId),
                mapOf(
                    "municipalityId" to progress.municipalityId,
                    "isUnlocked" to progress.isUnlocked,
                    "localUpdatedAt" to progress.localUpdatedAt,
                    "updatedAt" to Timestamp.now()
                ),
                SetOptions.merge()
            )
        }
        quizProgress.forEach { progress ->
            batch.set(
                usersRef.document(uid).collection("quiz_results").document(progress.municipalityId),
                mapOf(
                    "municipalityId" to progress.municipalityId,
                    "score" to progress.score,
                    "total" to progress.total,
                    "passed" to progress.passed,
                    "percentage" to progress.percentage,
                    "localUpdatedAt" to progress.localUpdatedAt,
                    "updatedAt" to Timestamp.now()
                ),
                SetOptions.merge()
            )
        }
        arProgress.forEach { progress ->
            val documentId = "${progress.municipalityId}__${progress.mode}"
            batch.set(
                usersRef.document(uid).collection("ar_progress").document(documentId),
                mapOf(
                    "municipalityId" to progress.municipalityId,
                    "mode" to progress.mode,
                    "placedCount" to progress.placedCount,
                    "totalCount" to progress.totalCount,
                    "completed" to progress.completed,
                    "localUpdatedAt" to progress.localUpdatedAt,
                    "updatedAt" to Timestamp.now()
                ),
                SetOptions.merge()
            )
        }
        batch.commit().await()
    }

    fun getTopPlayers(limit: Long = 10): Flow<List<Player>> = callbackFlow {
        val listener = leaderboardRef
            .orderBy("pvpPoints", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val players = snapshot?.documents?.mapIndexed { index, doc ->
                    Player(
                        uid                   = doc.getString("uid") ?: "",
                        displayName           = doc.getString("displayName") ?: "Unknown",
                        totalScore            = (doc.getLong("totalScore") ?: 0L).toInt(),
                        municipalitiesUnlocked= (doc.getLong("municipalitiesUnlocked") ?: 1L).toInt(),
                        quizAccuracy          = (doc.getDouble("quizAccuracy") ?: 0.0).toFloat(),
                        rank                  = index + 1,
                        pvpPoints             = (doc.getLong("pvpPoints") ?: 0L).toInt(),
                        onlineWins            = (doc.getLong("onlineWins") ?: 0L).toInt(),
                        onlineMatches         = (doc.getLong("onlineMatches") ?: 0L).toInt()
                    )
                } ?: emptyList()
                trySend(players)
            }
        awaitClose { listener.remove() }
    }

    private fun calculatePvpPoints(totalScore: Int, municipalitiesCompleted: Int, accuracy: Float): Int {
        if (totalScore <= 0) return 0
        val accuracyBonus = accuracy.toInt().coerceIn(0, 100) * 2
        val progressBonus = municipalitiesCompleted.coerceAtLeast(1) * 75
        return totalScore + accuracyBonus + progressBonus
    }

    private fun profileData(profile: OnlineUserProfile): Map<String, Any> = mapOf(
        "uid" to profile.uid,
        "username" to profile.username,
        "email" to profile.email,
        "totalScore" to profile.totalScore,
        "pvpPoints" to profile.pvpPoints,
        "municipalitiesUnlocked" to profile.municipalitiesUnlocked,
        "unlockedMunicipalityIds" to profile.unlockedMunicipalityIds,
        "quizAccuracy" to profile.quizAccuracy,
        "onlineWins" to profile.onlineWins,
        "onlineMatches" to profile.onlineMatches,
        "updatedAt" to Timestamp.now()
    )

    private fun leaderboardData(profile: OnlineUserProfile): Map<String, Any> = mapOf(
        "uid" to profile.uid,
        "displayName" to profile.username,
        "totalScore" to profile.totalScore,
        "pvpPoints" to profile.pvpPoints,
        "municipalitiesUnlocked" to profile.municipalitiesUnlocked,
        "quizAccuracy" to profile.quizAccuracy,
        "onlineWins" to profile.onlineWins,
        "onlineMatches" to profile.onlineMatches,
        "updatedAt" to Timestamp.now()
    )

    private fun progressProfileData(profile: OnlineUserProfile): Map<String, Any> = mapOf(
        "uid" to profile.uid,
        "username" to profile.username,
        "email" to profile.email,
        "totalScore" to profile.totalScore,
        "pvpPoints" to profile.pvpPoints,
        "quizAccuracy" to profile.quizAccuracy,
        "updatedAt" to Timestamp.now()
    )

    private fun leaderboardProgressData(profile: OnlineUserProfile): Map<String, Any> = mapOf(
        "uid" to profile.uid,
        "displayName" to profile.username,
        "totalScore" to profile.totalScore,
        "pvpPoints" to profile.pvpPoints,
        "municipalitiesUnlocked" to profile.municipalitiesUnlocked,
        "quizAccuracy" to profile.quizAccuracy,
        "updatedAt" to Timestamp.now()
    )

    private fun List<FirebaseMunicipalityProgress>.toMutableMapByMunicipalityId():
        MutableMap<String, FirebaseMunicipalityProgress> =
        associateByTo(linkedMapOf()) { it.municipalityId }

    private fun requireCurrentUserOwns(uid: String) {
        val authenticatedUid = currentUser?.uid
            ?: error("Firebase Authentication has no current user.")
        check(authenticatedUid == uid) {
            "Refusing to access a profile that does not belong to the authenticated user."
        }
    }

    /**
     * Firestore stores integral numbers as Long. Parse fields explicitly so old
     * documents with omitted optional fields remain readable, while a profile
     * whose UID disagrees with its document path is rejected rather than used.
     */
    private fun DocumentSnapshot.toOnlineUserProfile(expectedUid: String): OnlineUserProfile? {
        if (!exists()) return null

        val storedUid = getString("uid")
            ?: throw ProfileDataException("Profile $expectedUid is missing its uid field.")
        if (storedUid != expectedUid) {
            throw ProfileDataException("Profile uid does not match its Firestore document id.")
        }

        val unlockedMunicipalityIds = stringListField("unlockedMunicipalityIds")
        return OnlineUserProfile(
            uid = storedUid,
            username = getString("username")?.trim().orEmpty().ifBlank { "Explorer" },
            email = getString("email").orEmpty(),
            totalScore = intField("totalScore", 0),
            pvpPoints = intField("pvpPoints", 0),
            // The list is the durable summary. Count records from older app
            // versions can lag behind a queued array-union write.
            municipalitiesUnlocked = maxOf(
                intField("municipalitiesUnlocked", 1),
                unlockedMunicipalityIds.size
            ),
            unlockedMunicipalityIds = unlockedMunicipalityIds,
            quizAccuracy = floatField("quizAccuracy", 0f),
            onlineWins = intField("onlineWins", 0),
            onlineMatches = intField("onlineMatches", 0)
        )
    }

    private fun DocumentSnapshot.intField(field: String, default: Int): Int = when (val value = get(field)) {
        null -> default
        is Number -> value.toLong().coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
        else -> throw ProfileDataException("Profile field $field must be a number.")
    }

    private fun DocumentSnapshot.floatField(field: String, default: Float): Float = when (val value = get(field)) {
        null -> default
        is Number -> value.toFloat()
        else -> throw ProfileDataException("Profile field $field must be a number.")
    }

    private fun DocumentSnapshot.stringListField(field: String): List<String> {
        val values = get(field) ?: return emptyList()
        if (values !is List<*>) {
            throw ProfileDataException("Profile field $field must be an array of municipality ids.")
        }
        return values.mapIndexed { index, value ->
            value as? String
                ?: throw ProfileDataException("Profile field $field[$index] must be a string.")
        }.distinct()
    }
}

private class ProfileDataException(message: String) : IllegalStateException(message)
