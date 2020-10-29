package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.data.remote.NetworkErrorHandler
import com.diskin.alon.movieguide.news.data.remote.RemoteNewsStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.net.URLEncoder

/**
 * [RemoteNewsStore] uni test class.
 */
class RemoteNewsStoreTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var store: RemoteNewsStore

    // Collaborators
    private val api: FeedlyApi = mockk()
    private val apiArticleMapper: Mapper<List<FeedlyEntryResponse>, Result<ArticleEntity>> = mockk()
    private val networkErrorHandler: NetworkErrorHandler = mockk()

    @Before
    fun setUp() {
        // Init test subject
        store = RemoteNewsStore(api,apiArticleMapper,networkErrorHandler)
    }

    @Test
    fun createArticleResultApiWhenQueried() {
        // Test case fixture
        mockkStatic(URLEncoder::class)

        val encodedId = "encoded_id"
        val apiResponse = listOf(mockk<FeedlyEntryResponse>())
        val articleEntityResult = mockk<Result.Success<ArticleEntity>>()

        every { URLEncoder.encode(any(),"UTF-8") } returns encodedId
        every { api.getEntry(encodedId) } returns Single.just(apiResponse)
        every { apiArticleMapper.map(apiResponse) } returns articleEntityResult

        // Given an initialized store

        // When store is queried for article
        val articleId = "id"
        val testObserver = store.getArticle(articleId).test()

        // Then store should encode article id arg
        verify { URLEncoder.encode(articleId,"UTF-8") }

        // And store should get entry from api with encoded article id
        verify { api.getEntry(encodedId) }

        // And return mapped api result
        testObserver.assertValue(articleEntityResult)
    }

    @Test
    fun resolveApiErrorWhenQueriedAndApiFail() {
        // Test case fixture
        val apiSubject = SingleSubject.create<List<FeedlyEntryResponse>>()
        val appError = mockk<AppError>()

        every { api.getEntry(any()) } returns apiSubject
        every { networkErrorHandler.handle(any()) } returns appError

        // Given an initialized store

        // When store is queried for article
        val testObserver = store.getArticle("id").test()

        // And api throws an error
        val apiError = Throwable()
        apiSubject.onError(apiError)

        // Then store should ask network error handler to resolve error
        verify { networkErrorHandler.handle(apiError) }

        // And should return mapped error result
        testObserver.assertValue(Result.Error(appError))
    }
}