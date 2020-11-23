package com.diskin.alon.movieguide.news.featuretesting

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diskin.alon.movieguide.news.data.local.Bookmark
import io.reactivex.Observable

@Dao
interface TestBookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg headlines: Bookmark)

    @Query("SELECT * FROM bookmark")
    fun getAll(): Observable<List<Bookmark>>
}