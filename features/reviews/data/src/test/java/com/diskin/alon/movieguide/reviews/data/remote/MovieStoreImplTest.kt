package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse.MovieResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * [MovieStoreImpl] unit test class.
 */
class MovieStoreImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var store: MovieStoreImpl

    // Collaborators
    private val api: TheMovieDbApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()
    private val mapper: Mapper<MovieResponse, MovieEntity> = mockk()

    @Before
    fun setUp() {
        store = MovieStoreImpl(api, networkErrorHandler, mapper)
    }

    @Test
    fun loadMovieFromRemoteApiWhenQueried() {
        // Test case fixture
        val mappedMovie: MovieEntity = mockk()
        val apiResponse: MovieResponse = mockk()

        every { mapper.map(any()) } returns mappedMovie
        every { api.getMovie(any(),any()) } returns Single.just(apiResponse)

        // Given an initialized store

        // When store is asked to fetch a movie
        val id = "12"
        val testObserver = store.get(id).test()

        // Then store should ask remote api to fetch the wanted movie
        verify { api.getMovie(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }

        // And map api response
        verify { mapper.map(apiResponse) }

        // And propagate mapped remote result
        testObserver.assertValue(Result.Success(mappedMovie))
    }

    @Test
    fun handleErrorWhenLoadingMovieFromRemoteFail() {
        // Test case fixture
        val error: Throwable = mockk()
        val appError: AppError = mockk()

        every { api.getMovie(any(),any()) } returns Single.error(error)
        every { networkErrorHandler.handle(any()) } returns appError

        // Given an initialized store

        // When store is asked to fetch a movie
        val id = "12"
        val testObserver = store.get(id).test()

        // Then store should ask remote api to fetch the wanted movie
        verify { api.getMovie(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }

        // When loading fail

        // Then store should delegate error to error handler
        verify { networkErrorHandler.handle(error) }

        // And propagate error result
        testObserver.assertValue(Result.Error(appError))
    }
}