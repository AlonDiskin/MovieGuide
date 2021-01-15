package com.diskin.alon.movieguide.reviews.featuretesting.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovie
import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovieDao

@Database(entities = [FavoriteMovie::class], version = 1, exportSchema = false)
abstract class TestDatabase : RoomDatabase(){

    abstract fun favoritesDao(): FavoriteMovieDao

    abstract fun testFavoriteMovieDao(): TestFavoriteMovieDao
}