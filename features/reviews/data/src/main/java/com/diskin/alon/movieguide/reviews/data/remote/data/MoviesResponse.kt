package com.diskin.alon.movieguide.reviews.data.remote.data

/**
 * 'The Movie DB' api response model
 */
data class MoviesResponse(val page: Int,
                          val results: List<MovieResponse>,
                          val total_pages: Int
) {

    data class MovieResponse(val id: Int,
                             val title: String,
                             val release_date: String,
                             val poster_path: String,
                             val popularity: Double,
                             val vote_average: Double)
}
