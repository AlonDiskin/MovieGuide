package com.diskin.alon.movieguide.reviews.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface FavoriteMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg favoriteMovie: FavoriteMovie): Completable

    @Query("DELETE FROM favorites WHERE id = :id")
    fun delete(id: String): Completable

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE id = :id)")
    fun contains(id : String) : Observable<Boolean>

    @Query("SELECT * FROM favorites")
    fun getAll(): PagingSource<Int, FavoriteMovie>
}
