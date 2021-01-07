package com.diskin.alon.movieguide.reviews.domain.entities

import com.diskin.alon.movieguide.common.domain.Entity

/**
 * Movie entity class.
 */
class MovieEntity(
    id: String,
    var title: String,
    var popularity: Double,
    var rating: Double,
    var releaseDate: Long?,
    var posterUrl: String) : Entity<String>(id) {

    init {
        require(rating in 0.0..10.0)
        require(popularity >= 0)
    }
}