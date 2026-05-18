package com.example.lakbayaral.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LakbayAralDao {

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUser(userId: String = LOCAL_USER_ID): UserEntity?

    @Query("SELECT * FROM users WHERE lower(username) = lower(:username) ORDER BY updatedAt DESC LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY updatedAt DESC")
    suspend fun getUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("SELECT * FROM municipality_progress WHERE userId = :userId")
    suspend fun getMunicipalityProgress(userId: String = LOCAL_USER_ID): List<MunicipalityProgressEntity>

    @Query("SELECT * FROM municipality_progress WHERE userId = :userId AND municipalityId = :municipalityId LIMIT 1")
    suspend fun getMunicipalityProgress(municipalityId: String, userId: String = LOCAL_USER_ID): MunicipalityProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMunicipalityProgress(progress: MunicipalityProgressEntity)

    @Query("DELETE FROM municipality_progress WHERE userId = :userId")
    suspend fun deleteMunicipalityProgress(userId: String)

    @Query("SELECT * FROM ar_progress WHERE userId = :userId AND municipalityId = :municipalityId AND mode = :mode LIMIT 1")
    suspend fun getARProgress(municipalityId: String, mode: String, userId: String = LOCAL_USER_ID): ARProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertARProgress(progress: ARProgressEntity)

    @Query("DELETE FROM ar_progress WHERE userId = :userId")
    suspend fun deleteARProgress(userId: String)

    @Query("SELECT * FROM quiz_results WHERE userId = :userId")
    suspend fun getQuizResults(userId: String = LOCAL_USER_ID): List<QuizResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuizResult(result: QuizResultEntity)

    @Query("DELETE FROM quiz_results WHERE userId = :userId")
    suspend fun deleteQuizResults(userId: String)

    @Query("SELECT * FROM leaderboard ORDER BY totalScore DESC, updatedAt ASC LIMIT :limit")
    suspend fun getLeaderboard(limit: Int): List<LeaderboardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLeaderboardEntry(entry: LeaderboardEntity)

    @Query("DELETE FROM leaderboard WHERE userId = :userId")
    suspend fun deleteLeaderboardEntry(userId: String)
}
