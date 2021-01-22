package com.diskin.alon.movieguide

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovie
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovieDao

@Database(entities = [Bookmark::class,FavoriteMovie::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun bookmarkDao(): BookmarkDao

    abstract fun favoriteMovieDao(): FavoriteMovieDao
}