package com.diskin.alon.movieguide.reviews.domain.entities

import com.diskin.alon.movieguide.common.domain.Entity
import com.diskin.alon.movieguide.reviews.domain.value.MovieGenre
import com.diskin.alon.movieguide.reviews.domain.value.Trailer

/**
 * Movie review entity class.
 */
class MovieReviewEntity(
    id: String,
    var title: String,
    var rating: Double,
    var releaseDate: Long,
    var backDropImageUrl: String,
    var genres: List<MovieGenre>,
    var summary: String,
    var review: String,
    var webUrl: String,
    var trailersUrl: List<Trailer> = emptyList(),
) : Entity<String>(id) {

    init {
        require(rating in 0.0..10.0)
    }
}