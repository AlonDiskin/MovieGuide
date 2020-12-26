package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.BookmarksRequest
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.usecase.GetBookmarkedHeadlinesUseCase
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [GetBookmarkedHeadlinesUseCase] unit test class.
 */
class GetBookmarkedHeadlinesUseCaseTest {

    // Test subject
    private lateinit var useCase: GetBookmarkedHeadlinesUseCase

    // Collaborators
    private val repository: ArticleRepository = mockk()
    private val mapper: Mapper<List<ArticleEntity>, List<HeadlineDto>> = mockk()

    @Before
    fun setUp() {
        useCase = GetBookmarkedHeadlinesUseCase(repository,mapper)
    }

    @Test
    fun fetchBookmarkedHeadlinesWhenExecuted() {
        // Test case fixture
        val repositoryBookmarked: List<ArticleEntity> = mockk()
        val mappedBookmarked: List<HeadlineDto> = emptyList()

        every { repository.getBookmarked(any()) } returns Observable.just(repositoryBookmarked).toResult()
        every { mapper.map(any()) } returns mappedBookmarked

        // Given an initialized use case

        // When use case is executed
        val request = BookmarksRequest(mockk())
        val testObserver = useCase.execute(request).test()

        // Then use case should get bookmarked headlines from repository according to use case request
        verify { repository.getBookmarked(request.sorting) }

        // And map results to client data model
        verify { mapper.map(repositoryBookmarked) }
        testObserver.assertValue(Result.Success(mappedBookmarked))
    }
}