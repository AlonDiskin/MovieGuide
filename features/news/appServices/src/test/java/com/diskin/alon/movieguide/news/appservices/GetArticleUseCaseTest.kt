package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.news.appservices.data.ArticleRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [GetArticleUseCase] unit test.
 */
class GetArticleUseCaseTest {

    // Test subject
    private lateinit var useCase: GetArticleUseCase

    // Collaborators
    private val repository: ArticleRepository = mockk()

    @Before
    fun setUp() {
        // Init subject
        useCase = GetArticleUseCase(repository)
    }

    @Test
    fun getArticleDataWhenExecuted() {
        // Test case fixture
        val repoArticle = createArticle()
        val repoIsBookmarked = true

        every { repository.get(any()) } returns Observable.just(repoArticle).toResult()
        every { repository.isBookmarked(any()) } returns Observable.just(repoIsBookmarked).toResult()

        // Given an initialized useCase

        // When use case executed with valid request param
        val request = ArticleRequest("id")
        val testObserver = useCase.execute(request).test()

        // Then use case should get article from repository for id param
        verify { repository.get(request.id) }

        // And check in repository if article is bookmarked
        verify { repository.isBookmarked(request.id) }

        // And emmit article dto that holds expected article info
        val expectedDto = createArticleDto(repoArticle,repoIsBookmarked)
        testObserver.assertValue(Result.Success(expectedDto))
    }
}