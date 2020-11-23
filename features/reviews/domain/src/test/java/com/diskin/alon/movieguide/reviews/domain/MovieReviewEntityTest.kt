package com.diskin.alon.movieguide.reviews.domain

import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MovieReviewEntity] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieReviewEntityTest {

    // Test subject
    private lateinit var movieReview: MovieReviewEntity

    @Test(expected = IllegalArgumentException::class)
    @Parameters(method = "ratingInitParams")
    fun throwExceptionWhenInitializedWithOutOfRangeRating(rating: Double) {
        // Given uninitialized movie

        // When review initialized with rating value that is out of 0 - 10 range
        movieReview = MovieReviewEntity(
            "id",
            "title",
            rating,
            100L,
            "url",
            emptyList(),"summary","review","web_url" ,emptyList())

        // Then movie should throw an IllegalArgumentException
    }

    private fun ratingInitParams() = arrayOf(-0.1,10.1)
}