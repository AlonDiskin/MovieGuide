package com.diskin.alon.movieguide.reviews.domain.entities

import com.diskin.alon.movieguide.common.domain.Entity

/**
 * Movie entity class.
 */
class MovieEntity(
    override val id: String,
    val title: String,
    val popularity: Double,
    val rating: Double,
    val releaseDate: Long,
    val posterUrl: String) : Entity<String> {

    init {
        require(rating in 0.0..10.0)
        require(popularity >= 0)
    }
}