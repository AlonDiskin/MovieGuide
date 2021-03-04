package com.diskin.alon.movieguide.news.infrastructure

import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.MOVIES_NEWS_FEED
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * [RemoteRecentDateProviderImpl] unit test class.
 */
class RemoteRecentDateProviderImplTest {

    // Test subject
    private lateinit var provider: RemoteRecentDateProviderImpl

    // Collaborators
    private val feedlyApi: FeedlyApi = mockk()

    @Before
    fun setUp() {
        provider = RemoteRecentDateProviderImpl(feedlyApi)
    }

    @Test
    fun getLatestPublishedArticleDateWhenQueriedForDate() {
        // Test case fixture
        val apiResponse = createFeedlyResponseForRecentArticle()
        every { feedlyApi.getFeedItems(any(),any()) } returns Single.just(apiResponse)

        // Given an initialized provider

        // When provider queried for recent remote published  date
        val testObserver = provider.getDate().test()

        // Then provider should retrieve latest published article date from api
        verify { feedlyApi.getFeedItems(MOVIES_NEWS_FEED,1) }

        // And propagate expected result from remote date
        testObserver.assertValue(Date(apiResponse.items.first().published))
    }
}