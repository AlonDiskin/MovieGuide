package com.diskin.alon.movieguide.di

import android.app.Application
import androidx.room.Room
import com.diskin.alon.movieguide.AppDatabase
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.di.common.NewsDataModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NewsDataModule::class])
object DataModule {

    @JvmStatic
    @Singleton
    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app,
            AppDatabase::class.java, "movie-guide-db")
            .build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}