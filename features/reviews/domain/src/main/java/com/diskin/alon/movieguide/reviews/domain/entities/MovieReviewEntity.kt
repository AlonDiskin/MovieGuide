package com.diskin.alon.movieguide.reviews.domain.entities

import com.diskin.alon.movieguide.common.domain.Entity
import com.diskin.alon.movieguide.reviews.domain.value.MovieGenre
import com.diskin.alon.movieguide.reviews.domain.value.Trailer

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
    val webUrl: String,
    val trailersUrl: List<Trailer> = emptyList(),
) : Entity<String> {

    init {
        require(rating in 0.0..10.0)
    }
}