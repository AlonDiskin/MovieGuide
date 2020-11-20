package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import com.diskin.alon.movieguide.news.domain.ArticleEntity
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
    private val articleMapper: Mapper<Result<ArticleEntity>, Result<ArticleDto>> = mockk()

    @Before
    fun setUp() {
        // Init subject
        useCase = GetArticleUseCase(repository,articleMapper)
    }

    @Test
    fun getArticleFromRepositoryWhenExecuted() {
        // Test case fixture
        val repoArticleResult = mockk<Result<ArticleEntity>>()
        val mappedArticleResult = mockk<Result<ArticleDto>>()

        every { repository.get(any()) } returns Observable.just(repoArticleResult)
        every { articleMapper.map(any()) } returns mappedArticleResult

        // Given an initialized useCase

        // When use case executed with valid request param
        val request = ArticleRequest("id")
        val testObserver = useCase.execute(request).test()

        // Then use case should get article result from repository for id param
        verify { repository.get(request.id) }

        // And return mapped repository result article
        testObserver.assertValue(mappedArticleResult)
    }
}