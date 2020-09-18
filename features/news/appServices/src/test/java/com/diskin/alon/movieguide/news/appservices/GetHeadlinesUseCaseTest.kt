package com.diskin.alon.movieguide.news.appservices

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.news.appservices.Mapper.mapPagedHeadlinesToDto
import io.mockk.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [GetHeadlinesUseCaseTest] unit test class.
 */
class GetHeadlinesUseCaseTest {

    // System under test
    private lateinit var useCase: GetHeadlinesUseCase

    // Collaborators
    private val repository: HeadlineRepository = mockk()

    @Before
    fun setUp() {
        // Init subject under test
        useCase = GetHeadlinesUseCase(repository)
    }

    @Test
    fun getHeadlinesPagingWhenExecuted() {
        // Test case fixture
        mockkObject(Mapper)

        val testRepoHeadlines = createHeadlines()
        val testPageConfig = PagingConfig(pageSize = 10)
        val testRepoPaging = PagingData.from(testRepoHeadlines)
        val testMappedPaging = PagingData.empty<HeadlineDto>()

        every { repository.getPaging(testPageConfig) } returns
                Observable.just(testRepoPaging)

        every { mapPagedHeadlinesToDto(testRepoPaging) } returns testMappedPaging

        // Given an initialized use case

        // When use case is executed
        val testRequest = HeadlinesRequest(testPageConfig)
        val testObserver = useCase.execute(testRequest).test()

        // Then use case should retrieve headlines from repository
        verify { repository.getPaging(testRequest.pagingConfig) }

        // And return an observable mapping repository headlines
        verify { mapPagedHeadlinesToDto(testRepoPaging) }

        testObserver.assertValue(testMappedPaging)
    }
}