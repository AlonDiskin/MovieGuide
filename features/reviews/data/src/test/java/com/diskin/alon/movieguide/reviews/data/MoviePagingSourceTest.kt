package com.diskin.alon.movieguide.reviews.data

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.data.remote.MoviePagingSource
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.data.remote.TheMovieDbApi
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [MoviePagingSource] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MoviePagingSourceTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var pagingSource: MoviePagingSource

    // Collaborators
    private val api: TheMovieDbApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()
    private val mapper: Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> = mockk()

    // Stub data
    private val apiMoviesSubject = SingleSubject.create<MoviesResponse>()
    private val mappedMovies = emptyList<MovieEntity>()

    @Before
    fun setUp() {
        every { api.getByPopularity(any()) } returns apiMoviesSubject
        every { api.getByRating(any()) } returns apiMoviesSubject
        every { api.getByReleaseDate(any()) } returns apiMoviesSubject
        every { mapper.map(any()) } returns mappedMovies
    }

    @Test
    @Parameters(method = "sortParams")
    fun loadMoviesFromApiWhenLoadingToRefresh(sorting: MovieSorting) {
        // Given an initialized paging source
        pagingSource = MoviePagingSource(api,networkErrorHandler,sorting,mapper)

        // When paging source is refreshed
        val loadParams = LoadParams.Refresh<String>(null,20,false)
        val testObserver = pagingSource.loadSingle(loadParams).test()

        // Then paging source should ask api to load movies based on sorting param
        when(sorting) {
            MovieSorting.RATING -> verify { api.getByRating(1) }
            MovieSorting.RELEASE_DATE -> verify { api.getByReleaseDate(1) }
            MovieSorting.POPULARITY -> verify { api.getByPopularity(1) }
        }

        // When api loads movies
        val apiResponse = MoviesResponse(1, emptyList(),500)
        apiMoviesSubject.onSuccess(apiResponse)

        // Then paging source should map api response to expected paging result
        testObserver.assertValue { it is PagingSource.LoadResult.Page }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).data == mappedMovies }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).nextKey == (apiResponse.page + 1).toString() }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).prevKey == null }
    }

    @Test
    @Parameters(method = "sortParams")
    fun loadMoviesPageFromApiWhenLoadingToAppend(sorting: MovieSorting) {
        // Given an initialized paging source
        pagingSource = MoviePagingSource(api,networkErrorHandler,sorting,mapper)

        // When paging source loading append page
        val loadParams = LoadParams.Append("10",20,false)
        val testObserver = pagingSource.loadSingle(loadParams).test()

        // Then paging source should ask api to load movies based on sorting param and given key
        when(sorting) {
            MovieSorting.RATING -> verify { api.getByRating(loadParams.key.toInt()) }
            MovieSorting.RELEASE_DATE -> verify { api.getByReleaseDate(loadParams.key.toInt()) }
            MovieSorting.POPULARITY -> verify { api.getByPopularity(loadParams.key.toInt()) }
        }

        // When api loads movies
        val apiResponse = MoviesResponse(loadParams.key.toInt(), emptyList(),500)
        apiMoviesSubject.onSuccess(apiResponse)

        // Then paging source should map api response to expected paging result
        testObserver.assertValue { it is PagingSource.LoadResult.Page }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).data == mappedMovies }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).nextKey == (apiResponse.page + 1).toString() }
        testObserver.assertValue { (it as PagingSource.LoadResult.Page).prevKey == null }
    }

    @Test
    @Parameters(method = "pageResultNextKeyParams")
    fun calcPagingResultKeyAccordingToApiResponseWhenApiLoadMovies(
        sorting: MovieSorting,
        params: LoadParams<String>,
        apiResponse: MoviesResponse,
        key: String?
    ) {
        // Given an initialized paging source
        pagingSource = MoviePagingSource(api,networkErrorHandler,sorting,mapper)

        // When paging source asked to load movies
        val testObserver = pagingSource.loadSingle(params).test()

        // And remote api load movies
        apiMoviesSubject.onSuccess(apiResponse)

        // Then paging source return page result that contain next page key, according to api response
        testObserver.assertValue {
            val page = (it as PagingSource.LoadResult.Page<String, MovieEntity>)
            page.nextKey.equals(key)
        }
    }

    @Test
    @Parameters(method = "apiFailParams")
    fun handleApiErrorWhenApiFailToLoadMovies(
        params: LoadParams<String>,
        sorting: MovieSorting
    ) {
        // Test case fixture
        val appError = AppError("error description",true)
        every { networkErrorHandler.handle(any()) } returns appError

        // Given an initialized paging source
        pagingSource = MoviePagingSource(api,networkErrorHandler,sorting,mapper)

        // When paging source loading movies
        val testObserver = pagingSource.loadSingle(params).test()

        // And remote api client fail to load movies
        val apiError = Throwable()
        apiMoviesSubject.onError(apiError)

        // Then paging source should ask network handler to handle error
        verify { networkErrorHandler.handle(apiError) }

        // And return throwable containing the error description from handled error
        testObserver.assertValue {
            val result = it as PagingSource.LoadResult.Error
            val errorMessage = result.throwable.message!!

            errorMessage == appError.description
        }
    }

    private fun sortParams() = arrayOf(
        MovieSorting.RELEASE_DATE,
        MovieSorting.POPULARITY,
        MovieSorting.RELEASE_DATE
    )

    private fun pageResultNextKeyParams() = arrayOf(
        arrayOf(
            MovieSorting.RATING,
            LoadParams.Refresh<String>(null,20,false),
            MoviesResponse(300, emptyList(),500),
            "301"
        ),
        arrayOf(
            MovieSorting.RELEASE_DATE,
            LoadParams.Refresh<String>(null,20,false),
            MoviesResponse(500, emptyList(),500),
            null
        ),
        arrayOf(
            MovieSorting.RATING,
            LoadParams.Append("10",20,false),
            MoviesResponse(30, emptyList(),500),
            "31"
        ),
        arrayOf(
            MovieSorting.RATING,
            LoadParams.Append("10",20,false),
            MoviesResponse(100, emptyList(),100),
            null
        )
    )

    private fun apiFailParams() = arrayOf(
        arrayOf(
            LoadParams.Refresh<String>(null,20,false),
            MovieSorting.RATING
        ),
        arrayOf(
            LoadParams.Refresh<String>(null,20,false),
            MovieSorting.RELEASE_DATE
        ),
        arrayOf(
            LoadParams.Append("10",20,false),
            MovieSorting.RATING
        )
    )
}