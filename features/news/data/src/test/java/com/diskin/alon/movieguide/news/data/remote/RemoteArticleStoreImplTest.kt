package com.diskin.alon.movieguide.news.data.remote

import com.diskin.alon.movieguide.common.appservices.AppError
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.data.createFeedlyEntries
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyArticleId
import com.diskin.alon.movieguide.news.data.remote.data.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
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
import retrofit2.HttpException

/**
 * [RemoteArticleStoreImpl] unit test class.
 */
class RemoteArticleStoreImplTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var store: RemoteArticleStoreImpl

    // Collaborators
    private val api: FeedlyApi = mockk()
    private val errorHandler: NetworkErrorHandler = mockk()
    private val apiArticleMapper: Mapper<FeedlyEntryResponse, ArticleEntity> = mockk()

    @Before
    fun setUp() {
        store = RemoteArticleStoreImpl(api, errorHandler, apiArticleMapper)
    }

    @Test
    fun getArticleFromRemoteWhenQueriedAndArticleLoaded() {
        // Test case fixture
        val apiResponse: List<FeedlyEntryResponse> = listOf(mockk())
        val mappedArticle: ArticleEntity = mockk()

        every { api.getEntry(any()) } returns Single.just(apiResponse)
        every { apiArticleMapper.map(any()) } returns mappedArticle

        // Given

        // When
        val id = "id"
        val testObserver = store.getArticle(id).test()

        // Then store should fetch article from remote api
        verify { api.getEntry(id) }

        // And map remote result when api load response via mapper
        verify { apiArticleMapper.map(apiResponse.first()) }

        // And store should emit the expected result from mapper
        testObserver.assertValue(Result.Success(mappedArticle))
    }

    @Test
    fun handleRemoteErrorWhenArticleLoadFail() {
        // Test case fixture
        val remoteError: HttpException = mockk()
        val handledError = AppError("description",true)

        every { api.getEntry(any()) } returns Single.error(remoteError)
        every { errorHandler.handle(any()) } returns handledError

        // Given

        // When
        val testObserver = store.getArticle("id").test()

        // Then
        verify { errorHandler.handle(remoteError) }

        // And
        testObserver.assertValue(Result.Error(handledError))
    }

    @Test
    fun getArticlesFromRemoteWhenQueriedAndArticleLoaded() {
        // Test case fixture
        val apiResponse = createFeedlyEntries()
        val mappedArticle: ArticleEntity = mockk()

        every { api.getEntries(any()) } returns Single.just(apiResponse)
        every { apiArticleMapper.map(any()) } returns mappedArticle

        // Given an initialized store

        // When store is asked to fetch all id matching articles
        val ids = arrayOf("id1","id2")
        val testObserver = store.getAll(*ids).test()

        // Then store should fetch remote api articles
        verify { api.getEntries(ids.map { FeedlyArticleId(it) }) }

        // And map each one via article mapper
        apiResponse.forEach { verify { apiArticleMapper.map(it) } }

        // And return all mapped articles to clint
        testObserver.assertValue(Result.Success(apiResponse.map { mappedArticle }))
    }

    @Test
    fun handleRemoteErrorWhenArticlesLoadFail() {
        // Test case fixture
        val remoteError: HttpException = mockk()
        val handledError = AppError("description",true)

        every { api.getEntries(any()) } returns Single.error(remoteError)
        every { errorHandler.handle(any()) } returns handledError

        // Given an initialized store

        // When
        val testObserver = store.getAll(*emptyArray()).test()

        // Then
        verify { errorHandler.handle(remoteError) }

        // And
        testObserver.assertValue(Result.Error(handledError))
    }
}