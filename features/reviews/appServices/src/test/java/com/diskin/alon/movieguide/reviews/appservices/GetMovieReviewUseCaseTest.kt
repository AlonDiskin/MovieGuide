package com.diskin.alon.movieguide.reviews.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewRequest
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetMovieReviewUseCase
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 * [GetMovieReviewUseCase] unit test class.
 */
class GetMovieReviewUseCaseTest {

    // Test subject
    private lateinit var useCase: GetMovieReviewUseCase

    // Collaborators
    private val repository: MovieReviewRepository = mockk()
    private val mapper: Mapper<Result<MovieReviewEntity>, Result<MovieReviewDto>> = mockk()

    @Before
    fun setUp() {
        // Init test subject
        useCase = GetMovieReviewUseCase(repository,mapper)
    }

    @Test
    fun fetchReviewWhenExecuted() {
        // Test case fixture
        val repoReview = mockk<Result<MovieReviewEntity>>()
        val mapperReview = mockk<Result<MovieReviewDto>>()

        every { repository.getReview(any()) } returns Observable.just(repoReview)
        every { mapper.map(any()) } returns mapperReview
        // Given an initialized use case

        // When use case is executed
        val request = MovieReviewRequest("id")
        val testObserver = useCase.execute(request).test()

        // Then use case should fetch review observable from repository
        verify { repository.getReview(request.id) }

        // And return mapped result model
        verify { mapper.map(repoReview) }
        testObserver.assertValue(mapperReview)
    }
}