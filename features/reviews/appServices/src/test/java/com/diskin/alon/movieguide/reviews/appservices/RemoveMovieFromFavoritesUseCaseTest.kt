package com.diskin.alon.movieguide.reviews.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.appservices.data.RemoveFavoriteMovieRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.usecase.RemoveMovieFromFavoritesUseCase
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

/**
 * [RemoveMovieFromFavoritesUseCase] unit test class.
 */
class RemoveMovieFromFavoritesUseCaseTest {

    // Test subject
    private lateinit var useCase: RemoveMovieFromFavoritesUseCase

    // Collaborators
    private val repository: MovieRepository = mockk()

    @Before
    fun setUp() {
        useCase = RemoveMovieFromFavoritesUseCase(repository)
    }

    @Test
    fun removeMovieFromFavoritesWhenExecuted() {
        // Test case fixture
        val repoResult = Single.just(mockk<Result<Unit>>())
        every { repository.removeFromFavorites(any()) } returns repoResult

        // Given an initialized use case

        // When use case is executed with suitable request data
        val request = RemoveFavoriteMovieRequest("id")
        val useCaseResult = useCase.execute(request)

        // Then use case should ask movie repository remove movie from favorites
        verify { repository.removeFromFavorites(request.movieId) }

        // And return repo result
        Truth.assertThat(useCaseResult).isEqualTo(repoResult)
    }
}