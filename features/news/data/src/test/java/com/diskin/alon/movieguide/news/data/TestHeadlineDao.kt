package com.diskin.alon.movieguide.news.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.diskin.alon.movieguide.news.data.local.data.Bookmark

@Dao
interface TestHeadlineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg headlines: Bookmark)
}