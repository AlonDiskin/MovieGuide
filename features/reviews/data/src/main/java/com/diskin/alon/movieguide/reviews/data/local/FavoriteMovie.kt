package com.diskin.alon.movieguide.reviews.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteMovie(@PrimaryKey val id: String,
                         val title: String,
                         val popularity: Double,
                         val rating: Double,
                         val releaseDate: Long,
                         val posterUrl: String)