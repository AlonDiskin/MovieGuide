package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.data.BuildConfig
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
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
 * [MovieReviewStoreImpl] unit test class.
 */
class MovieReviewStoreImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var source: MovieReviewStoreImpl

    // Collaborators
    private val api: TheMovieDbApi = mockk()
    private val mapper: Mapper2<MovieDetailResponse, TrailersResponse, MovieReviewEntity> = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()

    @Before
    fun setUp() {
        // Init test subject
        source = MovieReviewStoreImpl(api, mapper, networkErrorHandler)
    }

    @Test
    fun loadReviewFromRemoteApiWhenQueried() {
        // Test case fixture
        val apiMovieDetail = mockk<MovieDetailResponse>()
        val apiTrailers = mockk<TrailersResponse>()
        val mappedResult = mockk<MovieReviewEntity>()

        every { api.getMovieDetail(any(),any()) } returns Single.just(apiMovieDetail)
        every { api.getTrailers(any(),any()) } returns Single.just(apiTrailers)
        every { mapper.map(any(),any()) } returns mappedResult

        // Given an initialized source

        // When source is queried for movie review
        val id = "1"
        val testObserver = source.getReview(id).test()

        // Then source should load review form remote api with api key and review id
        verify { api.getMovieDetail(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }
        verify { api.getTrailers(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }

        // And map api results
        verify { mapper.map(apiMovieDetail,apiTrailers) }
        testObserver.assertValue(Result.Success(mappedResult))
    }

    @Test
    fun handleErrorWhenApiReviewLoadFail() {
        // Test case fixture
        val apiMovieDetail = mockk<MovieDetailResponse>()
        val apiError = Throwable()
        val handledError = mockk<AppError>()

        every { api.getMovieDetail(any(),any()) } returns Single.just(apiMovieDetail)
        every { api.getTrailers(any(),any()) } returns Single.error(apiError)
        every { networkErrorHandler.handle(any()) } returns handledError

        // Given an initialized remote source

        // When source is queried for movie review
        val id = "1"
        val testObserver = source.getReview(id).test()

        // Then source should load review form remote api with api key and review id
        verify { api.getMovieDetail(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }
        verify { api.getTrailers(id.toInt(), BuildConfig.MOVIE_DB_API_KEY) }

        // When remote api fail to load review source should map load error via network handler
        verify { networkErrorHandler.handle(any()) }

        // And remote source should return handled error
        testObserver.assertValue{ it is Result.Error}
        testObserver.assertValue {
            val result = it as Result.Error<MovieReviewEntity>

            result.error == handledError
        }
    }
}