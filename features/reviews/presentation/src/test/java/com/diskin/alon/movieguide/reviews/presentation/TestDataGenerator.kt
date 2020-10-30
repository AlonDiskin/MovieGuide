package com.diskin.alon.movieguide.reviews.presentation

import com.diskin.alon.movieguide.reviews.presentation.model.Movie

fun createMovies(): List<Movie> {
    return listOf(
        Movie(
            "id1",
            "title1",
            "url1",
            "7.8"
        ),
        Movie(
            "id2",
            "title2",
            "url2",
            "6.3"
        ),
        Movie(
            "id3",
            "title3",
            "url3",
            "8.7"
        )
    )
}