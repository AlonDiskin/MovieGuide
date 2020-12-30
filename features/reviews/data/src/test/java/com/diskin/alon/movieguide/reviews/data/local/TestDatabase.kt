package com.diskin.alon.movieguide.reviews.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteMovie::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase(){

    abstract fun favoritesDao(): FavoriteMovieDao
}