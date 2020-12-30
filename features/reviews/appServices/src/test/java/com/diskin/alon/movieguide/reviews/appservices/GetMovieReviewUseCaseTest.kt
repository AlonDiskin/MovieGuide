package com.diskin.alon.movieguide.reviews.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.appservices.usecase.GetMovieReviewUseCase
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
    private val reviewRepo: MovieReviewRepository = mockk()
    private val movieRepo: MovieRepository = mockk()

    @Before
    fun setUp() {
        // Init test subject
        useCase = GetMovieReviewUseCase(reviewRepo,movieRepo)
    }

    @Test
    fun fetchReviewWhenExecuted() {
        // Test case fixture
        val repoReview = createMovieReviewEntity()
        val repoFavorite = true

        every { reviewRepo.getReview(any()) } returns Observable.just(repoReview).toResult()
        every { movieRepo.isFavorite(any()) } returns Observable.just(repoFavorite).toResult()

        // Given an initialized useCase

        // When use case executed with valid request param
        val request = MovieReviewRequest("id")
        val testObserver = useCase.execute(request).test()

        // Then use case should get review from repository with request id param
        verify { reviewRepo.getReview(request.id) }

        // And check in movie repository if reviewed movie is a user favorite
        verify { movieRepo.isFavorite(request.id) }

        // And transform results into review dto
        val expectedDto = createMovieReviewDto(repoReview,repoFavorite)
        testObserver.assertValue(Result.Success(expectedDto))
    }
}