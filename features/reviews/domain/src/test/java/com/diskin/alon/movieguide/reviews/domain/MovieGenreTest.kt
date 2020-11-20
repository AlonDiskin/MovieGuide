package com.diskin.alon.movieguide.reviews.domain

import com.diskin.alon.movieguide.reviews.domain.value.MovieGenre
import org.junit.Test

/**
 * [MovieGenre] test class.
 */
class MovieGenreTest {

    // Test subject
    private lateinit var genre: MovieGenre

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenCreatedWithEmptyGenreName() {
        // Given an uninitialized genre

        // When genre is created with empty genre name value
        genre = MovieGenre("")

        // Then genre should throw an IllegalArgumentException
    }
}