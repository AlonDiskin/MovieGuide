package com.diskin.alon.movieguide.news.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.data.implementation.ArticleRepositoryImpl
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [ArticleRepositoryImpl] unit test
 */
class ArticleRepositoryImplTest {

    // Test subject
    private lateinit var repository: ArticleRepositoryImpl

    // Collaborators
    private val articleStore: RemoteArticleStore = mockk()

    @Before
    fun setUp() {
        // Init subject
        repository = ArticleRepositoryImpl(articleStore)
    }

    @Test
    fun getArticleFromRemoteStoreWhenQueried() {
        // Test case fixture
        val articleStoreResult = mockk<Result<ArticleEntity>>()
        every { articleStore.getArticle(any()) } returns Observable.just(articleStoreResult)

        // Given an initialized repository

        // When repository is queried for article
        val articleId = "id"
        val testObserver = repository.get(articleId).test()

        // Then repository should get entry from remote article store
        verify { articleStore.getArticle(articleId) }

        // And return store result
        testObserver.assertValue(articleStoreResult)
    }
}