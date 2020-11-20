package com.diskin.alon.movieguide.reviews.domain.entities

import com.diskin.alon.movieguide.common.domain.Entity
import com.diskin.alon.movieguide.reviews.domain.value.MovieGenre

/**
 * Movie review entity class.
 */
class MovieReviewEntity(
    override val id: String,
    val title: String,
    val rating: Double,
    val releaseDate: Long,
    val backDropImageUrl: String,
    val genres: List<MovieGenre>,
    val summary: String,
    val review: String,
    val trailersUrl: List<String> = emptyList()
) : Entity<String> {

    init {
        require(rating in 0.0..10.0)
    }
}