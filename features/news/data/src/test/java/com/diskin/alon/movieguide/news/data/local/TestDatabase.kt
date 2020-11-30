package com.diskin.alon.movieguide.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diskin.alon.movieguide.news.data.local.data.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase(){

    abstract fun headlineDao(): BookmarkDao
}