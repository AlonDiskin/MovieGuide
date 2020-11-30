package com.diskin.alon.movieguide

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.data.local.data.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase(){
    abstract fun bookmarkDao(): BookmarkDao
}