package com.diskin.alon.movieguide.news.data

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.news.data.local.MoviesHeadlinesPagingSource
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.FeedlyFeedResponse
import com.diskin.alon.movieguide.news.data.remote.MOVIES_NEWS_FEED
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import com.diskin.alonmovieguide.common.data.NetworkErrorHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import junitparams.JUnitParamsRunner
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * [MoviesHeadlinesPagingSource] unit test class.
 */
@RunWith(JUnitParamsRunner::class)
class MoviesHeadlinesPagingSourceTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var pagingSource: MoviesHeadlinesPagingSource

    // Collaborators
    private val api: FeedlyApi = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()

    @Before
    fun setUp() {
        // Init subject
        pagingSource =  MoviesHeadlinesPagingSource(api,networkErrorHandler)
    }

    @Test
    fun loadFirstRemotePageWhenInitiallyLoaded() {
        // Test case fixture
        val apiResponse = getMappedFeedlyResponse()
        every { api.getFeedItems(MOVIES_NEWS_FEED,any()) } returns Single.just(apiResponse)

        // Given an initialized paging source

        // When paging source is loading first requested page (api class should call it with null)
        // key value,and place holder value not relevant for test case)
        val params = LoadParams.Refresh<String>(null,20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should load sized feed from api client
        verify { api.getFeedItems(MOVIES_NEWS_FEED,params.loadSize) }

        // And map single emission from api response to LoadResult
        testObserver.assertValue{ verifyPagingSourceMapLoadResult(it,apiResponse)}
    }

    @Test
    fun loadKeyedRemotePageWhenContinuallyLoaded() {
        // Test case fixture
        val apiResponse = getMappedFeedlyResponse()
        every { api.getFeedItemsPage(MOVIES_NEWS_FEED,any(),any()) } returns Single.just(apiResponse)

        // Given an initialized paging source

        // When paging source is loading subsequent page
        // key value,and place holder value not relevant for test case)
        val params = LoadParams.Append("key",20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should load sized feed page from api client
        verify { api.getFeedItemsPage(MOVIES_NEWS_FEED, params.key,params.loadSize) }

        // And map single emission from api response to LoadResult
        testObserver.assertValue{ verifyPagingSourceMapLoadResult(it,apiResponse)}
    }

    @Test
    fun loadKeyedRemoteLastPageWhenInitiallyLoaded() {
        // Test case fixture
        val apiResponse = getMappedFeedlyLastPageResponse()
        every { api.getFeedItems(MOVIES_NEWS_FEED,any()) } returns Single.just(apiResponse)

        // Given an initialized paging source

        // When paging source is loading first requested page (api class should call it with null), which
        // is also the last available page
        // key value,and place holder value not relevant for test case)
        val params = LoadParams.Refresh<String>(null,20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should load sized feed page from api client
        verify { api.getFeedItems(MOVIES_NEWS_FEED, params.loadSize) }

        // And map single emission from api response to LoadResult
        testObserver.assertValue{ verifyPagingSourceMapLoadResult(it,apiResponse)}
    }

    @Test
    fun loadKeyedRemoteLastPageWhenContinuallyLoaded() {
        // Test case fixture
        val apiResponse = getMappedFeedlyLastPageResponse()
        every { api.getFeedItemsPage(MOVIES_NEWS_FEED,any(),any()) } returns Single.just(apiResponse)

        // Given an initialized paging source

        // When paging source is loading subsequent last available page
        // key value,and place holder value not relevant for test case)
        val params = LoadParams.Append("key",20,false)
        val testObserver = pagingSource.loadSingle(params).test()

        // Then paging source should load sized feed page from api client
        verify { api.getFeedItemsPage(MOVIES_NEWS_FEED, params.key,params.loadSize) }

        // And map single emission from api response to LoadResult
        testObserver.assertValue{ verifyPagingSourceMapLoadResult(it,apiResponse)}
    }

    @Test
    fun returnMappedErrorResultWhenApiLoadFailUponSourceRefresh() {
        // Test case fixture
        val apiError = Throwable()
        val appError = AppError("error description",true)

        every { api.getFeedItems(any(),any()) } returns Single.error(apiError)
        every { networkErrorHandler.handle(any()) } returns appError

        // Given an initialized paging source

        // When api call fail during paging source refresh
        val refreshParams = LoadParams.Refresh("key",10,false)
        val testObserver =
            pagingSource.loadSingle(refreshParams).test()

        // Then paging source should ask network error handler api error
        verify { networkErrorHandler.handle(apiError) }

        // And return throwable containing the error description from handled error
        testObserver.assertValue {
            val result = it as PagingSource.LoadResult.Error
            val errorMessage = result.throwable.message!!

            errorMessage == appError.cause
        }
    }

    @Test
    fun returnMappedErrorResultWhenApiLoadFailUponSourceAppend() {
        // Test case fixture
        val apiError = Throwable()
        val appError = AppError("error description",true)

        every { api.getFeedItemsPage(any(),any(),any()) } returns Single.error(apiError)
        every { networkErrorHandler.handle(any()) } returns appError

        // Given an initialized paging source

        // When api call fail during paging source paige append
        val refreshParams = LoadParams.Append("key",10,false)
        val testObserver =
            pagingSource.loadSingle(refreshParams).test()

        // Then paging source should ask network error handler api error
        verify { networkErrorHandler.handle(apiError) }

        // And return throwable containing the error description from handled error
        testObserver.assertValue {
            val result = it as PagingSource.LoadResult.Error
            val errorMessage = result.throwable.message!!

            errorMessage == appError.cause
        }
    }

    private fun verifyPagingSourceMapLoadResult(loadResult: PagingSource.LoadResult<String, HeadlineEntity>,
                                                apiResponse: FeedlyFeedResponse
    ): Boolean {
        val result: PagingSource.LoadResult.Page<String,HeadlineEntity> =
            loadResult as PagingSource.LoadResult.Page<String, HeadlineEntity>
        val expectedNextKey = apiResponse.continuation
        val expectedPrevKey = null
        val expectedHeadlines = apiResponse.items.map { entry ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = entry.published
            HeadlineEntity(
                entry.id,
                entry.title,
                entry.published,
                entry.visual?.url ?: "",
                entry.originId
            )
        }

        // assert paging source mapping api response feed entries
        result.data.forEachIndexed { i , actualHeadline ->
            val expectedHeadline = expectedHeadlines[i]
            if (actualHeadline.id != expectedHeadline.id ||
                actualHeadline.title != expectedHeadline.title ||
                actualHeadline.imageUrl != expectedHeadline.imageUrl ||
                actualHeadline.date != expectedHeadline.date) {
                return false
            }
        }

        // assert result include next page key
        return (result.nextKey == expectedNextKey) && (result.prevKey == expectedPrevKey)
    }
}