package com.diskin.alon.movieguide.reviews.featuretesting.di

import android.app.Application
import androidx.room.Room
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestDataModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): TestDatabase {
        return Room.inMemoryDatabaseBuilder(
            app, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideHeadlineDao(database: TestDatabase): FavoriteMovieDao {
        return database.favoritesDao()
    }
}