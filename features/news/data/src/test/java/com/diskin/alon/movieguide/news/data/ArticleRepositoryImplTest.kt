package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.implementation.cleanId
import com.diskin.alon.movieguide.news.data.implementation.mapApiEntryResponseToArticleEntity
import com.diskin.alon.movieguide.news.data.remote.FeedlyApi
import com.diskin.alon.movieguide.news.data.remote.FeedlyEntryResponse
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

/**
 * [ArticleRepositoryImpl] unit test
 */
class ArticleRepositoryImplTest {

    // Test subject
    private lateinit var repository: ArticleRepositoryImpl

    // Collaborators
    private val api = mockk<FeedlyApi>()

    @Before
    fun setUp() {
        // Init subject
        repository = ArticleRepositoryImpl(api)
    }

    @Test
    fun getArticleWhenQueried() {
        // Test case fixture
        mockkStatic("com.diskin.alon.movieguide.news.data.implementation.EntryIdCleanerKt")
        mockkStatic("com.diskin.alon.movieguide.news.data.implementation.MapperKt")

        val cleanId = "cleanedId"
        val testApiResponse = listOf(mockk<FeedlyEntryResponse>())
        val testArticleEntity = mockk<ArticleEntity>()
        every { cleanId(any()) } returns cleanId
        every { api.getEntry(cleanId) } returns Single.just(testApiResponse)
        every { mapApiEntryResponseToArticleEntity(any()) } returns testArticleEntity

        // Given

        // When repository is queried for article
        val testId = "id"
        val testObserver = repository.get(testId).test()

        // Then repository should get entry from api with cleaned id
        verify { cleanId(testId) }
        verify { api.getEntry(cleanId) }

        // And return mapped api result
        verify { mapApiEntryResponseToArticleEntity(testApiResponse.first()) }
        testObserver.assertValue(testArticleEntity)
    }
}