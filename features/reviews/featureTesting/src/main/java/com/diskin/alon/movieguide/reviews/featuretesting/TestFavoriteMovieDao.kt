package com.diskin.alon.movieguide.reviews.featuretesting

import androidx.room.Dao
import androidx.room.Insert
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovie

@Dao
interface TestFavoriteMovieDao {
    @Insert
    fun insert(vararg favoriteMovie: FavoriteMovie)
}