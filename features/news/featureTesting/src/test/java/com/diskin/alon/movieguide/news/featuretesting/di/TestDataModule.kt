package com.diskin.alon.movieguide.news.featuretesting.di

import android.app.Application
import androidx.room.Room
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
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
    fun provideHeadlineDao(database: TestDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}