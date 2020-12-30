package com.diskin.alon.movieguide.reviews.data.remote

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.MovieResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity

/**
 * [MovieMapper] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieMapperTest {

    // Test subject
    private lateinit var mapper: MovieMapper

    @Before
    fun setUp() {
        mapper = MovieMapper()
    }

    @Test
    @Parameters(method = "mapParams")
    fun mapToEntities(apiMovie: MovieResponse, movie: MovieEntity) {
        // Given

        // When
        val actualMapped = mapper.map(apiMovie)

        // Then
        assertThat(actualMapped).isEqualTo(movie)
    }
    private fun mapParams() = arrayOf(
        arrayOf(
            MovieResponse(
                123,
                "title1",
                "2012-08-03",
                "/poster/path/6",
                456.987,
                2.3
            ),
            MovieEntity(
                "123",
                "title1",
                456.987,
                2.3,
                LocalDate.parse("2012-08-03").toDate().time,
                "http://image.tmdb.org/t/p/w342/poster/path/6"
            )
        ),
        arrayOf(
            MovieResponse(
                13,
                "title2",
                "2020-10-23",
                "/poster/path/23",
                1156.987,
                8.3
            ),
            MovieEntity(
                "13",
                "title2",
                1156.987,
                8.3,
                LocalDate.parse("2020-10-23").toDate().time,
                "http://image.tmdb.org/t/p/w342/poster/path/23"
            )
        )
    )
}