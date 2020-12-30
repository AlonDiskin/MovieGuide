package com.diskin.alon.movieguide.reviews.featuretesting

import android.app.Application
import androidx.room.Room
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovieDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestDataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDatabase(app: Application): TestDatabase {
        return Room.inMemoryDatabaseBuilder(
            app, TestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideHeadlineDao(database: TestDatabase): FavoriteMovieDao {
        return database.favoritesDao()
    }
}