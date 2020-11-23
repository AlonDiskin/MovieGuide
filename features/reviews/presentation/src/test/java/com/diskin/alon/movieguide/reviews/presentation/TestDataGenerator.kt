package com.diskin.alon.movieguide.reviews.presentation

import com.diskin.alon.movieguide.reviews.presentation.data.Movie
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer

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

fun createTestReview(): MovieReview {
    return MovieReview(
        "id",
        "title",
        "8.9",
        "Horror,Comedy",
        "22-10-2020",
        "summary",
        "review",
        "url",
        "webUrl",
        listOf(Trailer("url1","url2"),Trailer("url3","url4"))
    )
}