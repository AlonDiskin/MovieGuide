package com.diskin.alon.movieguide.reviews.data.remote

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.data.createMovieApiResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
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
 * [MoviesSearchPagingSource] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MoviesSearchPagingSourceTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var source: MoviesSearchPagingSource

    // Collaborators
    private val api: TheMovieDbApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()
    private val query: String = "query"
    private val mapper: Mapper<MoviesResponse.MovieResponse, MovieEntity> = mockk()

    // Stub data
    private val apiSubject = SingleSubject.create<MoviesResponse>()

    @Before
    fun setUp() {
        // Stub collaborators
        every { api.search(any(),any()) } returns apiSubject

        source = MoviesSearchPagingSource(api, networkErrorHandler, query, mapper)
    }

    @Test
    fun loadSearchResultsFromApiWhenLoadingToRefresh() {
        // Test case fixture
        val mappedMovie = mockk<MovieEntity>()
        every { mapper.map(any()) } returns mappedMovie

        // Given

        // When source asked to load a fresh page of search results
        val params: LoadParams.Refresh<String> = mockk()
        val testObserver = source.loadSingle(params).test()

        // Then source should ask the first page of search results from api
        verify { api.search(query,1) }

        // When api loads results
        val apiResponse = createMovieApiResponse()
        apiSubject.onSuccess(apiResponse)

        // Then source should propagate mapped api response
        testObserver.assertValue(
            LoadResult.Page(
                apiResponse.results.map { mappedMovie },
                null,
                (apiResponse.page + 1).toString(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED
            )
        )
    }

    @Test
    fun loadSearchResultsFromApiWhenLoadingToAppend() {
        // Test case fixture
        val mappedMovie = mockk<MovieEntity>()
        every { mapper.map(any()) } returns mappedMovie

        // Given

        // When source asked to load an appended page of search results
        val params = LoadParams.Append("2",20,false)
        val testObserver = source.loadSingle(params).test()

        // Then source should ask an indexed page of search results from api, based on load params key value
        verify { api.search(query,params.key.toInt()) }

        // When api loads results
        val apiResponse = createMovieApiResponse()
        apiSubject.onSuccess(apiResponse)

        // Then source should propagate mapped api response
        testObserver.assertValue(
            LoadResult.Page(
                apiResponse.results.map { mappedMovie },
                null,
                (apiResponse.page + 1).toString(),
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED
            )
        )
    }

    @Test
    @Parameters(method = "pageResultNextKeyParams")
    fun calcPagingResultKeyAccordingToApiResponseWhenApiLoadMovies(
        params: LoadParams<String>,
        apiResponse: MoviesResponse,
        key: String?
    ) {
        // Given

        // When paging source asked to load movies
        val testObserver = source.loadSingle(params).test()

        // And remote api load movies
        apiSubject.onSuccess(apiResponse)

        // Then paging source return page result that contain next page key, according to api response
        testObserver.assertValue {
            val page = (it as LoadResult.Page<String, MovieEntity>)
            page.nextKey.equals(key)
        }
    }

    private fun pageResultNextKeyParams() = arrayOf(
        arrayOf(
            LoadParams.Refresh<String>(null,20,false),
            MoviesResponse(300, emptyList(),500),
            "301"
        ),
        arrayOf(
            LoadParams.Refresh<String>(null,20,false),
            MoviesResponse(500, emptyList(),500),
            null
        ),
        arrayOf(
            LoadParams.Append("10",20,false),
            MoviesResponse(30, emptyList(),500),
            "31"
        ),
        arrayOf(
            LoadParams.Append("10",20,false),
            MoviesResponse(100, emptyList(),100),
            null
        )
    )
}