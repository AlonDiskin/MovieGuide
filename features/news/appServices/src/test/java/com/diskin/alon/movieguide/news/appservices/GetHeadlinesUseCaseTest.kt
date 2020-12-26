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
    private val mapper: Mapper<PagingData<ArticleEntity>, PagingData<HeadlineDto>> = mockk()

    @Before
    fun setUp() {
        // Init subject under test
        useCase = GetHeadlinesUseCase(repository,mapper)
    }

    @Test
    fun getHeadlinesPagingWhenExecuted() {
        // Test case fixture
        val repoPaging: PagingData<ArticleEntity> = mockk()
        val mappedPaging: PagingData<HeadlineDto> = mockk()

        every { repository.getPaging(any()) } returns Observable.just(repoPaging)
        every { mapper.map(any()) } returns mappedPaging

        // Given an initialized use case

        // When use case is executed
        val request = HeadlinesRequest(PagingConfig(pageSize = 10))
        val testObserver = useCase.execute(request).test()

        // Then use case should retrieve headlines from repository
        verify { repository.getPaging(request.pagingConfig) }

        // And map repository paging
        verify { mapper.map(repoPaging) }

        // And emit mapped paging that has expected dtos
        testObserver.assertValue(mappedPaging)
    }
}