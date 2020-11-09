package com.diskin.alon.movieguide.reviews.appservices

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.data.MovieDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieSorting
import com.diskin.alon.movieguide.reviews.appservices.data.SortedMoviesRequest
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetSortedMoviesUseCase
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [GetSortedMoviesUseCase] unit test class.
 */
class GetSortedMoviesUseCaseTest {

    // Test subject
    private lateinit var useCase: GetSortedMoviesUseCase

    // Collaborators
    private val repository: MovieRepository = mockk()
    private val pagingMapper: Mapper<PagingData<MovieEntity>, PagingData<MovieDto>> = mockk()

    @Before
    fun setUp() {
        useCase = GetSortedMoviesUseCase(repository,pagingMapper)
    }

    @Test
    fun fetchSortedMoviesPagingWhenExecuted() {
        // Test case fixture
        val repoPaging = mockk<PagingData<MovieEntity>>()

        every { repository.getAllBySorting(any(),any()) } returns Observable.just(repoPaging)

        // Given an initialized use case

        //  When use case is executed
        val config = mockk<PagingConfig>()
        val sorting = mockk<MovieSorting>()
        val request = SortedMoviesRequest(config,sorting)
        useCase.execute(request).test()

        // Then use case should get movie entity paging from movies repo
        verify { repository.getAllBySorting(config, sorting) }

        // And map paging to dto paging
        verify { pagingMapper.map(repoPaging) }
    }
}