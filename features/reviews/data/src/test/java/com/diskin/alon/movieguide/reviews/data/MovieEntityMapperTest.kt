package com.diskin.alon.movieguide.reviews.data

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.diskin.alon.movieguide.reviews.data.MoviesResponse.MovieResponse
import com.diskin.alon.movieguide.reviews.domain.MovieEntity

/**
 * [MovieEntityMapper] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MovieEntityMapperTest {

    // Test subject
    private lateinit var mapper: MovieEntityMapper

    @Before
    fun setUp() {
        mapper = MovieEntityMapper()
    }

    @Test
    @Parameters(method = "mapParams")
    fun mapToEntities(apiMovies: List<MovieResponse>, entities: List<MovieEntity>) {
        // Given

        // When
        val actualMapped = mapper.map(apiMovies)

        // Then
        assertThat(actualMapped).isEqualTo(entities)
    }

    private fun mapParams() = arrayOf(
        arrayOf(listOf(
            MovieResponse(
                123,
                "title1",
                "2012-08-03",
                "/poster/path/6",
                456.987,
                2.3
            ),
            MovieResponse(
                13,
                "title2",
                "2020-10-23",
                "/poster/path/23",
                1156.987,
                8.3
            )
        ),
        listOf(
            MovieEntity(
                "123",
                "title1",
                456.987,
                2.3,
                LocalDate.parse("2012-08-03").toDate().time,
                "http://image.tmdb.org/t/p/w342/poster/path/6"
            ),
            MovieEntity(
                "13",
                "title2",
                1156.987,
                8.3,
                LocalDate.parse("2020-10-23").toDate().time,
                "http://image.tmdb.org/t/p/w342/poster/path/23"
            )
        ))
    )
}