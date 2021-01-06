package com.diskin.alon.movieguide.reviews.appservices.data

data class MovieReviewDto(val id: String,
                          val title: String,
                          val rating: Double,
                          val releaseDate: Long,
                          val backDropImageUrl: String,
                          val genres: List<String>,
                          val summary: String,
                          val review: String,
                          val webUrl: String,
                          val trailers: List<TrailerDto>,
                          val favorite: Boolean = false)


