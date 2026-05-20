package com.example.lakbayaral.data.repository

import com.example.lakbayaral.data.models.OnlineUserProfile
import com.example.lakbayaral.data.models.Player
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
            val profile = OnlineUserProfile(
                uid = uid,
                username = username.trim().ifBlank { "Explorer" },
                email = email.trim()
            )
            saveProfile(profile)
            profile
        }

    suspend fun signInWithEmail(email: String, password: String): Result<OnlineUserProfile> =
        runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val uid = result.user?.uid ?: error("Signed in without a user id.")
            getProfile(uid) ?: OnlineUserProfile(
                uid = uid,
                username = email.substringBefore("@").ifBlank { "Explorer" },
                email = email.trim()
            ).also { saveProfile(it) }
        }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun getProfile(uid: String = currentUser?.uid ?: ""): OnlineUserProfile? {
        if (uid.isBlank()) return null
        return usersRef.document(uid).get().await().toObject(OnlineUserProfile::class.java)
    }

    suspend fun saveProfile(profile: OnlineUserProfile) {
        val data = mapOf(
            "uid" to profile.uid,
            "username" to profile.username,
            "email" to profile.email,
            "totalScore" to profile.totalScore,
            "pvpPoints" to profile.pvpPoints,
            "municipalitiesUnlocked" to profile.municipalitiesUnlocked,
            "quizAccuracy" to profile.quizAccuracy,
            "onlineWins" to profile.onlineWins,
            "onlineMatches" to profile.onlineMatches,
            "updatedAt" to Timestamp.now()
        )
        usersRef.document(profile.uid).set(data, SetOptions.merge()).await()
    }

    suspend fun syncScore(
        playerName: String,
        totalScore: Int,
        municipalitiesCompleted: Int,
        accuracy: Float
    ): Result<Unit> = runCatching {
        val uid = currentUser?.uid ?: error("Sign in online before syncing your score.")
        val existing = getProfile(uid)
        val username = playerName.trim().ifBlank { existing?.username ?: "Explorer" }
        val syncedTotalScore = maxOf(totalScore, existing?.totalScore ?: 0)
        val syncedMunicipalities = maxOf(municipalitiesCompleted, existing?.municipalitiesUnlocked ?: 1)
        val syncedAccuracy = maxOf(accuracy, existing?.quizAccuracy ?: 0f)
        val pvpPoints = calculatePvpPoints(syncedTotalScore, syncedMunicipalities, syncedAccuracy)

        val profile = OnlineUserProfile(
            uid = uid,
            username = username,
            email = currentUser?.email ?: existing?.email.orEmpty(),
            totalScore = syncedTotalScore,
            pvpPoints = pvpPoints,
            municipalitiesUnlocked = syncedMunicipalities,
            quizAccuracy = syncedAccuracy,
            onlineWins = existing?.onlineWins ?: 0,
            onlineMatches = existing?.onlineMatches ?: 0
        )
        saveProfile(profile)

        val leaderboardEntry = hashMapOf(
            "uid" to uid,
            "displayName" to profile.username,
            "totalScore" to profile.totalScore,
            "pvpPoints" to profile.pvpPoints,
            "municipalitiesUnlocked" to profile.municipalitiesUnlocked,
            "quizAccuracy" to profile.quizAccuracy,
            "onlineWins" to profile.onlineWins,
            "onlineMatches" to profile.onlineMatches,
            "updatedAt" to Timestamp.now()
        )
        leaderboardRef.document(uid).set(leaderboardEntry, SetOptions.merge()).await()
    }

    suspend fun uploadScore(playerName: String, totalScore: Int, municipalitiesCompleted: Int, accuracy: Float) {
        try {
            syncScore(playerName, totalScore, municipalitiesCompleted, accuracy)
        } catch (e: Exception) {
            // Graceful degradation if offline.
        }
    }

    fun getTopPlayers(limit: Long = 10): Flow<List<Player>> = callbackFlow {
        val listener = leaderboardRef
            .orderBy("pvpPoints", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
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
}
