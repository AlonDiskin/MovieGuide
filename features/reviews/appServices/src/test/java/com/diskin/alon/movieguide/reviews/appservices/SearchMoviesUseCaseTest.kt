package com.diskin.alon.movieguide.reviews.appservices

import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.SearchMoviesRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.usecase.SearchMoviesUseCase
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [SearchMoviesUseCase] unit test class.
 */
class SearchMoviesUseCaseTest {

    // Test subject
    private lateinit var useCase: SearchMoviesUseCase

    // Collaborators
    private val repository: MovieRepository = mockk()
    private val mapper: Mapper<PagingData<MovieEntity>, PagingData<MovieDto>> = mockk()

    @Before
    fun setUp() {
        useCase = SearchMoviesUseCase(repository, mapper)
    }

    @Test
    fun performSearchForMoviesWhenExecuted() {
        // Test case fixture
        val paging = PagingData.empty<MovieEntity>()
        val repoResult = Observable.just(paging)
        val mappedPaging: PagingData<MovieDto> = mockk()

        every { repository.search(any(),any()) } returns repoResult
        every { mapper.map(any()) } returns mappedPaging

        // Given

        // When use case is executed
        val request = SearchMoviesRequest("query", mockk())
        val testObserver = useCase.execute(request).test()

        // Then use case should fetch movies paging from repo for searched query
        verify { repository.search(request.query,request.config) }

        // And propagate transformation of mapped paging
        verify { mapper.map(paging) }
        testObserver.assertValue(mappedPaging)
    }
}