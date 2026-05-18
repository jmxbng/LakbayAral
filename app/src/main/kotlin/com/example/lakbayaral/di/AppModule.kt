package com.example.lakbayaral.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.example.lakbayaral.data.local.LakbayAralDao
import com.example.lakbayaral.data.local.LakbayAralDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides @Singleton
    fun provideLakbayAralDatabase(
        @ApplicationContext context: Context
    ): LakbayAralDatabase = Room.databaseBuilder(
        context,
        LakbayAralDatabase::class.java,
        "lakbay_aral.db"
    )
        .addMigrations(MIGRATION_1_2)
        .build()

    @Provides @Singleton
    fun provideLakbayAralDao(database: LakbayAralDatabase): LakbayAralDao =
        database.lakbayAralDao()

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `municipality_progress_new` (
                    `userId` TEXT NOT NULL,
                    `municipalityId` TEXT NOT NULL,
                    `isUnlocked` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`userId`, `municipalityId`)
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO `municipality_progress_new` (`userId`, `municipalityId`, `isUnlocked`, `updatedAt`)
                SELECT 'local_user', `municipalityId`, `isUnlocked`, `updatedAt` FROM `municipality_progress`
                """.trimIndent()
            )
            database.execSQL("DROP TABLE `municipality_progress`")
            database.execSQL("ALTER TABLE `municipality_progress_new` RENAME TO `municipality_progress`")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `ar_progress_new` (
                    `userId` TEXT NOT NULL,
                    `municipalityId` TEXT NOT NULL,
                    `mode` TEXT NOT NULL,
                    `placedCount` INTEGER NOT NULL,
                    `totalCount` INTEGER NOT NULL,
                    `completed` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`userId`, `municipalityId`, `mode`)
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO `ar_progress_new` (`userId`, `municipalityId`, `mode`, `placedCount`, `totalCount`, `completed`, `updatedAt`)
                SELECT 'local_user', `municipalityId`, `mode`, `placedCount`, `totalCount`, `completed`, `updatedAt` FROM `ar_progress`
                """.trimIndent()
            )
            database.execSQL("DROP TABLE `ar_progress`")
            database.execSQL("ALTER TABLE `ar_progress_new` RENAME TO `ar_progress`")

            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `quiz_results_new` (
                    `userId` TEXT NOT NULL,
                    `municipalityId` TEXT NOT NULL,
                    `score` INTEGER NOT NULL,
                    `total` INTEGER NOT NULL,
                    `passed` INTEGER NOT NULL,
                    `percentage` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`userId`, `municipalityId`)
                )
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO `quiz_results_new` (`userId`, `municipalityId`, `score`, `total`, `passed`, `percentage`, `updatedAt`)
                SELECT 'local_user', `municipalityId`, `score`, `total`, `passed`, `percentage`, `updatedAt` FROM `quiz_results`
                """.trimIndent()
            )
            database.execSQL("DROP TABLE `quiz_results`")
            database.execSQL("ALTER TABLE `quiz_results_new` RENAME TO `quiz_results`")
        }
    }
}
