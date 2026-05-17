package com.example.lakbayaral.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.lakbayaral.data.models.QuizResult
import com.example.lakbayaral.data.models.UserProgress
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("lakbay_progress", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var _progress = loadProgress()

    val progress: UserProgress get() = _progress

    fun unlockMunicipality(id: String) {
        _progress.unlockedMunicipalities.add(id)
        saveProgress()
    }

    fun isMunicipalityUnlocked(id: String): Boolean =
        _progress.unlockedMunicipalities.contains(id)

    fun saveQuizResult(result: QuizResult) {
        _progress.quizResults[result.municipalityId] = result
        val newScore = _progress.totalScore + result.score * 10
        _progress = _progress.copy(totalScore = newScore)
        if (result.passed) unlockNextMunicipality(result.municipalityId)
        saveProgress()
    }

    private fun unlockNextMunicipality(currentId: String) {
        val municipalities = LocalDataSource.municipalities
        val currentIndex = municipalities.indexOfFirst { it.id == currentId }
        if (currentIndex >= 0 && currentIndex + 1 < municipalities.size) {
            val nextId = municipalities[currentIndex + 1].id
            unlockMunicipality(nextId)
        }
    }

    fun getTotalScore(): Int = _progress.totalScore

    private fun loadProgress(): UserProgress {
        val json = prefs.getString("progress_json", null) ?: return UserProgress()
        return try {
            gson.fromJson(json, UserProgress::class.java) ?: UserProgress()
        } catch (e: Exception) {
            UserProgress()
        }
    }

    private fun saveProgress() {
        prefs.edit().putString("progress_json", gson.toJson(_progress)).apply()
    }

    fun getPlayerName(): String = prefs.getString("player_name", "Explorer") ?: "Explorer"

    fun setPlayerName(name: String) {
        prefs.edit().putString("player_name", name).apply()
    }
}
