package com.example.lakbayaral.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String = LOCAL_USER_ID,
    val username: String = "Explorer",
    val totalScore: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "municipality_progress", primaryKeys = ["userId", "municipalityId"])
data class MunicipalityProgressEntity(
    val userId: String = LOCAL_USER_ID,
    val municipalityId: String,
    val isUnlocked: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ar_progress", primaryKeys = ["userId", "municipalityId", "mode"])
data class ARProgressEntity(
    val userId: String = LOCAL_USER_ID,
    val municipalityId: String,
    val mode: String,
    val placedCount: Int = 0,
    val totalCount: Int = 0,
    val completed: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_results", primaryKeys = ["userId", "municipalityId"])
data class QuizResultEntity(
    val userId: String = LOCAL_USER_ID,
    val municipalityId: String,
    val score: Int,
    val total: Int,
    val passed: Boolean,
    val percentage: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey val userId: String = LOCAL_USER_ID,
    val displayName: String,
    val totalScore: Int,
    val municipalitiesUnlocked: Int,
    val quizAccuracy: Float,
    val updatedAt: Long = System.currentTimeMillis()
)

const val LOCAL_USER_ID = "local_user"
