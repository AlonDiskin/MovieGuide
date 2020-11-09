package com.diskin.alon.movieguide.reviews.domain

import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MovieEntity] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieEntityTest {

    // Test subject
    private lateinit var movie: MovieEntity

    @Test(expected = IllegalArgumentException::class)
    @Parameters(method = "ratingInitParams")
    fun throwExceptionWhenInitializedWithOutOfRangeRating(rating: Double) {
        // Given uninitialized movie

        // When movie initialized with rating value that is out of 0 - 10 range
        movie = MovieEntity("id","title",100.0,rating,10L,"url")

        // Then movie should throw an IllegalArgumentException
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwExceptionWhenInitializedWithNegativePopularity() {
        // Given uninitialized movie

        // When movie initialized with negative popularity value
        movie = MovieEntity("id","title",-0.01,4.5,10L,"url")

        // Then movie should throw an IllegalArgumentException
    }

    private fun ratingInitParams() = arrayOf(-0.1,10.1)
}