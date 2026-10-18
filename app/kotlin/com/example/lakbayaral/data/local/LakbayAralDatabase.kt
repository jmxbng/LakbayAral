package com.example.lakbayaral.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        MunicipalityProgressEntity::class,
        ARProgressEntity::class,
        QuizResultEntity::class,
        LeaderboardEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LakbayAralDatabase : RoomDatabase() {
    abstract fun lakbayAralDao(): LakbayAralDao
}
