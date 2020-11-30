package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.news.appservices.data.BookmarkArticleRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.usecase.BookmarkArticleUseCase
import com.diskin.alon.movieguide.common.appservices.Result
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

/**
 * [BookmarkArticleUseCase] unit test class.
 */
class BookmarkArticleUseCaseTest {

    // Test subject
    private lateinit var useCase: BookmarkArticleUseCase

    // Collaborators
    private val repository: ArticleRepository = mockk()

    @Before
    fun setUp() {
        useCase = BookmarkArticleUseCase(repository)
    }

    @Test
    fun bookmarkArticleWhenExecuted() {
        // Test case fixture
        val repoResult = Single.just(mockk<Result<Unit>>())

        every { repository.bookmark(any()) } returns repoResult

        // Given an initialized use case

        // When use case is executed
        val request = BookmarkArticleRequest("id")
        val actual = useCase.execute(request)

        // Then use case should delegate bookmarking to repository,and return its operation result
        verify { repository.bookmark(request.id) }
        assertThat(actual).isEqualTo(repoResult)
    }
}