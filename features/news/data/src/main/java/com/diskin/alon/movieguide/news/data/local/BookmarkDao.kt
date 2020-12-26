package com.diskin.alon.movieguide.news.data.local

import androidx.room.*
import com.diskin.alon.movieguide.news.data.local.data.Bookmark
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg headlines: Bookmark): Completable

    @Query("SELECT * FROM bookmark ORDER BY id DESC")
    fun getAllDesc(): Observable<List<Bookmark>>

    @Query("SELECT * FROM bookmark")
    fun getAll(): Observable<List<Bookmark>>

    @Query("DELETE FROM bookmark WHERE articleId = :articleId")
    fun delete(articleId: String)

    @Transaction
    fun deleteAllByArticleId(ids: List<String>) {
        ids.forEach { delete(it) }
    }
}