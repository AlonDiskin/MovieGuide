package com.diskin.alon.movieguide.reviews.data

import androidx.paging.PagingConfig
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.data.implementations.MovieRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.TheMovieDbApi
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

/**
 * [MovieRepositoryImpl] uni test class.
 */
class MovieRepositoryImplTest {

    // Test subject
    private lateinit var repository: MovieRepositoryImpl

    // Collaborators
    private val api: TheMovieDbApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()
    private val mapper: Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> = mockk()

    @Before
    fun setUp() {
        repository = MovieRepositoryImpl(api, networkErrorHandler, mapper)
    }

    @Test
    fun getMoviesPagingBySortingWhenQueried() {
        // Given an initialized repository

        // When repository is queried for movies paging
        val sorting = MovieSorting.RELEASE_DATE
        val config = PagingConfig(10)
        val testObserver = repository.getAllBySorting(config,sorting).test()

        // Then repository should construct a pager that emits movies paging date
        testObserver.assertValueCount(1)
    }
}