package com.example.lakbayaral.data.repository

import com.example.lakbayaral.data.models.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val db = FirebaseFirestore.getInstance()
    private val leaderboardRef = db.collection("leaderboard")

    suspend fun signInAnonymously(): String? {
        return try {
            val result = FirebaseAuth.getInstance().signInAnonymously().await()
            result.user?.uid
        } catch (e: Exception) {
            null
        }
    }

    suspend fun uploadScore(playerName: String, totalScore: Int, municipalitiesCompleted: Int, accuracy: Float) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val player = hashMapOf(
            "uid"                    to uid,
            "displayName"            to playerName,
            "totalScore"             to totalScore,
            "municipalitiesUnlocked" to municipalitiesCompleted,
            "quizAccuracy"           to accuracy,
            "updatedAt"              to com.google.firebase.Timestamp.now()
        )
        try {
            leaderboardRef.document(uid).set(player).await()
        } catch (e: Exception) {
            // Graceful degradation if offline
        }
    }

    fun getTopPlayers(limit: Long = 10): Flow<List<Player>> = callbackFlow {
        val listener = leaderboardRef
            .orderBy("totalScore", Query.Direction.DESCENDING)
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
                        rank                  = index + 1
                    )
                } ?: emptyList()
                trySend(players)
            }
        awaitClose { listener.remove() }
    }
}
