package com.diskin.alon.movieguide.reviews.data

import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.MovieResponse
import com.diskin.alon.movieguide.reviews.data.remote.MovieMapper
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
    fun mapToEntities(apiMovies: List<MovieResponse>, movies: List<MovieEntity>) {
        // Given

        // When
        val actualMapped = mapper.map(apiMovies)

        // Then
        actualMapped.forEachIndexed { index, apiMovie ->
            val expected = movies[index]

            assertThat(apiMovie.id).isEqualTo(expected.id)
            assertThat(apiMovie.title).isEqualTo(expected.title)
            assertThat(apiMovie.popularity).isEqualTo(expected.popularity)
            assertThat(apiMovie.rating).isEqualTo(expected.rating)
            assertThat(apiMovie.releaseDate).isEqualTo(expected.releaseDate)
            assertThat(apiMovie.posterUrl).isEqualTo(expected.posterUrl)
        }
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