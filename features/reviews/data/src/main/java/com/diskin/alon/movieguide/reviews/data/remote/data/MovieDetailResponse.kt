package com.diskin.alon.movieguide.reviews.data.remote.data

/**
 * 'The Movie DB' api response model
 */
data class MovieDetailResponse(val id: Int,
                               val original_language: String,
                               val title: String,
                               val genres: List<Genre>,
                               val backdrop_path: String,
                               val vote_average: Double,
                               val release_date: String,
                               val overview: String) {

    data class Genre(var name: String)
}