package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.model.ArticleDto
import com.diskin.alon.movieguide.news.appservices.model.ArticleRequest
import com.diskin.alon.movieguide.news.appservices.usecase.GetArticleUseCase
import com.diskin.alon.movieguide.news.appservices.usecase.Mapper
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
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
    private val repository = mockk<ArticleRepository>()

    @Before
    fun setUp() {
        // Init subject
        useCase = GetArticleUseCase(repository)
    }

    @Test
    fun getArticleDtoWhenExecuted() {
        // Test case fixture
        mockkObject(Mapper)

        val testArticleEntity = mockk<ArticleEntity>()
        val testArticleDto = mockk<ArticleDto>()

        every { repository.get(any()) } returns Observable.just(testArticleEntity)
        every { Mapper.mapArticleEntity(any()) } returns testArticleDto

        // Given an initialized useCase

        // When use case executed with valid request param
        val testRequest = ArticleRequest("id")
        val testObserver = useCase.execute(testRequest).test()

        // Then use case should get article from repository with request id param
        verify { repository.get(testRequest.id) }

        // And return mapped repository article
        testObserver.assertValue(testArticleDto)
    }
}