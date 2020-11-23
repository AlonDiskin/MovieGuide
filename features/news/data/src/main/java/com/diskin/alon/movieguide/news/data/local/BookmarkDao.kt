package com.diskin.alon.movieguide.news.data.local

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmark ORDER BY id DESC")
    fun getHeadlinesDesc(): Observable<List<Bookmark>>

    @Query("SELECT * FROM bookmark")
    fun getHeadlinesAsc(): Observable<List<Bookmark>>
}