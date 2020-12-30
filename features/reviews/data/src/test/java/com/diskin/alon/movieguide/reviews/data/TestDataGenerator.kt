package com.diskin.alon.movieguide.reviews.data

import com.diskin.alon.movieguide.reviews.data.local.FavoriteMovie
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.*

fun createFavoriteMovie(): FavoriteMovie {
    return FavoriteMovie(
        "id",
        "title",
        7.8,
        3.4,
        34567L,
        "url"
    )
}

fun createMovieApiResponse(): MoviesResponse {
    return MoviesResponse(
        1,
        listOf(
            MovieResponse(
                13,
                "title2",
                "2020-10-23",
                "/poster/path/23",
                1156.987,
                8.3
            ),
            MovieResponse(
                123,
                "title1",
                "2012-08-03",
                "/poster/path/6",
                456.987,
                2.3
            )
        ),
        500
    )
}