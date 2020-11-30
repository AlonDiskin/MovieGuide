package com.diskin.alon.movieguide.news.data.implementation

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.news.data.local.BookmarkStore
import com.diskin.alon.movieguide.news.data.remote.RemoteArticleStore
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
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
    private val remoteStore: RemoteArticleStore = mockk()
    private val bookmarkStore: BookmarkStore = mockk()

    @Before
    fun setUp() {
        // Init subject
        repository = ArticleRepositoryImpl(remoteStore,bookmarkStore)
    }

    @Test
    fun getArticleFromRemoteStoreWhenQueried() {
        // Test case fixture
        val storeResult: Observable<Result<ArticleEntity>> = mockk()
        every { remoteStore.getArticle(any()) } returns storeResult

        // Given an initialized repository

        // When repository is queried for article
        val articleId = "id"
        val actual = repository.get(articleId)

        // Then repository should get entry from remote article store
        verify { remoteStore.getArticle(articleId) }

        // And return store result
        assertThat(actual).isEqualTo(storeResult)
    }

    @Test
    fun addArticleToBookmarkStoreWhenArticleBookmarked() {
        // Test case fixture
        val storeResult: Single<Result<Unit>> = mockk()
        every { bookmarkStore.add(any()) } returns storeResult

        // Given an initialized repository

        // When repository is asked to bookmark an article
        val id = "id"
        val actual = repository.bookmark(id)

        // Then repository should add article id to bookmarks store
        verify { bookmarkStore.add(id) }

        // And return store bookmarking result
        assertThat(actual).isEqualTo(storeResult)
    }

    @Test
    fun removeArticleFromBookmarkStoreWhenArticleBookmarked() {
        // Test fixture
        val storeResult: Single<Result<Unit>> = mockk()
        every { bookmarkStore.remove(any()) } returns storeResult

        // Given an initialized repository

        // When repository is asked to un bookmark an article
        val ids = listOf("id1","id2")
        val actual = repository.unBookmark(ids)

        // Then
        verify { bookmarkStore.remove(ids) }

        // And
        assertThat(actual).isEqualTo(storeResult)
    }
}