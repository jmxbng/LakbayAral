package com.example.lakbayaral.di

import android.content.Context
import androidx.room.Room
import com.example.lakbayaral.data.local.LakbayAralDao
import com.example.lakbayaral.data.local.LakbayAralDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    ).build()

    @Provides @Singleton
    fun provideLakbayAralDao(database: LakbayAralDatabase): LakbayAralDao =
        database.lakbayAralDao()
}
