package com.diskin.alon.movieguide.news.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.BookmarksRequest
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.interfaces.HeadlineRepository
import com.diskin.alon.movieguide.news.appservices.usecase.GetBookmarkedHeadlinesUseCase
import com.diskin.alon.movieguide.news.domain.HeadlineEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Test

/**
 * [GetBookmarkedHeadlinesUseCase] unit test class.
 */
class GetBookmarkedHeadlinesUseCaseTest {

    @Test
    fun fetchBookmarkedHeadlinesWhenExecuted() {
        // Test case fixture
        val repository: HeadlineRepository = mockk()
        val mapper: Mapper<Result<List<HeadlineEntity>>, Result<List<HeadlineDto>>> = mockk()
        val repositoryBookmarked: Result<List<HeadlineEntity>> = mockk()
        val mappedBookmarked: Result<List<HeadlineDto>> = mockk()

        every { repository.getBookmarked(any()) } returns Observable.just(repositoryBookmarked)
        every { mapper.map(any()) } returns mappedBookmarked

        // Given an initialized use case
        val useCase = GetBookmarkedHeadlinesUseCase(repository, mapper)

        // When use case is executed
        val request = BookmarksRequest(mockk())
        val testObserver = useCase.execute(request).test()

        // Then use case should get bookmarked headlines from repository according to use case request
        verify { repository.getBookmarked(request.sorting) }

        // And map results to client data model
        verify { mapper.map(repositoryBookmarked) }
        testObserver.assertValue(mappedBookmarked)
    }
}