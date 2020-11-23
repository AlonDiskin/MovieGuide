package com.diskin.alon.movieguide.news.featuretesting

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diskin.alon.movieguide.news.data.local.BookmarkDao
import com.diskin.alon.movieguide.news.data.local.Bookmark

@Database(entities = [Bookmark::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase(){
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun testBookmarkDao(): TestBookmarkDao
}