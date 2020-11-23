package com.diskin.alon.movieguide.news.featuretesting

import android.app.Application
import androidx.room.Room
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
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
    fun provideHeadlineDao(database: TestDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}