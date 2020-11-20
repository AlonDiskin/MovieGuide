package com.diskin.alon.movieguide.reviews.presentation.data

data class MovieReview(
    val id: String,
    val title: String,
    val rating: String,
    val genres: String,
    val releaseDate: String,
    val summary: String,
    val review: String,
    val backDropImageUrl: String,
    val trailersUrls: List<String>
)
