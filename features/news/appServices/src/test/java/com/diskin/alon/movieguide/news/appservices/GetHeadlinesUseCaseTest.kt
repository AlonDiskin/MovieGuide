package com.diskin.alon.movieguide.news.appservices

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.news.appservices.data.HeadlineDto
import com.diskin.alon.movieguide.news.appservices.data.HeadlinesRequest
import com.diskin.alon.movieguide.news.appservices.interfaces.ArticleRepository
import com.diskin.alon.movieguide.news.appservices.usecase.GetHeadlinesUseCase
import com.diskin.alon.movieguide.news.domain.ArticleEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    private val repository: ArticleRepository = mockk()
    private val headlinesMapper: Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>> = mockk()

    @Before
    fun setUp() {
        // Init subject under test
        useCase = GetHeadlinesUseCase(repository,headlinesMapper)
    }

    @Test
    fun getHeadlinesPagingWhenExecuted() {
        // Test case fixture

        val repoHeadlines = createHeadlines()
        val pageConfig = PagingConfig(pageSize = 10)
        val repoPaging = PagingData.from(repoHeadlines)
        val mappedPaging = PagingData.empty<HeadlineDto>()

        every { repository.getPaging(pageConfig) } returns
                Observable.just(repoPaging)

        every { headlinesMapper.map(repoPaging) } returns mappedPaging

        // Given an initialized use case

        // When use case is executed
        val testRequest = HeadlinesRequest(pageConfig)
        val testObserver = useCase.execute(testRequest).test()

        // Then use case should retrieve headlines from repository
        verify { repository.getPaging(testRequest.pagingConfig) }

        // And return an observable mapping repository headlines
        testObserver.assertValue(mappedPaging)
    }
}