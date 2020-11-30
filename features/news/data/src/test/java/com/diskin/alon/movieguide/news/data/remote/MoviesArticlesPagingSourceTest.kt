package com.diskin.alon.movieguide.news.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.data.createFeedlyFeedResponse
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * [MoviesArticlesPagingSource] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MoviesArticlesPagingSourceTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var pagingSource: MoviesArticlesPagingSource

    // Collaborators
    private val api: FeedlyApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()
    private val apiArticleMapper: Mapper<FeedlyEntryResponse, ArticleEntity> = mockk()

    @Before
    fun setUp() {
        // Init subject
        pagingSource =  MoviesArticlesPagingSource(api,networkErrorHandler,apiArticleMapper)
    }

    @Test
    fun loadArticlesFromApiWhenLoadingToRefresh() {
        // Test case fixture
        val apiResponse = createFeedlyFeedResponse()
        val mappedArticle: ArticleEntity = mockk()

        every { api.getFeedItems(any(),any()) } returns Single.just(apiResponse)
        every { apiArticleMapper.map(any()) } returns mappedArticle

        // Given an initialized paging source

        // When paging source load articles to refresh paging
        val params = LoadParams.Refresh<String>(null,20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should ask api to load articles based on load param
        verify { api.getFeedItems(MOVIES_NEWS_FEED,params.loadSize) }

        // And map api response to paging load result
        testObserver.assertValue(
            LoadResult.Page(
                apiResponse.items.map { mappedArticle },
                null,
                apiResponse.continuation,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED
            )
        )
    }

    @Test
    fun loadArticlesPageFromApiWhenLoadingToAppend() {
        // Test case fixture
        val apiResponse = createFeedlyFeedResponse()
        val mappedArticle: ArticleEntity = mockk()

        every { api.getFeedItemsPage(any(),any(),any()) } returns Single.just(apiResponse)
        every { apiArticleMapper.map(any()) } returns mappedArticle

        // Given an initialized paging source

        // When paging source loading append page
        val params = LoadParams.Append("key",20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should ask api to load articles based on load param
        verify { api.getFeedItemsPage(MOVIES_NEWS_FEED,params.key,params.loadSize) }

        // And map api response to paging load result
        testObserver.assertValue(
            LoadResult.Page(
                apiResponse.items.map { mappedArticle },
                null,
                apiResponse.continuation,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED
            )
        )
    }

    @Test
    @Parameters(method = "apiFailParams")
    fun handleApiErrorWhenApiFailToLoadMovies(
        params: LoadParams<String>,
        apiError: Throwable
    ) {
        // Test case fixture
        val appError = AppError("error description",true)

        every { networkErrorHandler.handle(any()) } returns appError
        every { api.getFeedItems(any(),any()) } returns Single.error(apiError)
        every { api.getFeedItemsPage(any(),any(),any()) } returns Single.error(apiError)

        // Given an initialized paging source

        // When paging source loads articles from remote api that fail to load
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should ask network handler to handle api loading error
        verify { networkErrorHandler.handle(apiError) }

        // And return error load result containing handles error
        testObserver.assertValue{
            val result = it as LoadResult.Error
            val errorMessage = result.throwable.message!!

            errorMessage == appError.description
        }
    }

    private fun apiFailParams() = arrayOf(
        arrayOf(
            LoadParams.Refresh<String>(null,20,false),
            IOException("error message")

        ),
        arrayOf(
            LoadParams.Append("10",20,false),
            IOException("error message")
        )
    )
}