package com.diskin.alon.movieguide.reviews.appservices

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.appservices.data.AddFavoriteMovieRequest
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.usecase.AddMovieToFavoritesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

/**
 * [AddMovieToFavoritesUseCase] unit test class.
 */
class AddMovieToFavoritesUseCaseTest {

    // Test subject
    private lateinit var useCase: AddMovieToFavoritesUseCase

    // Collaborators
    private val repository: MovieRepository = mockk()

    @Before
    fun setUp() {
        useCase = AddMovieToFavoritesUseCase(repository)
    }

    @Test
    fun addMovieToUserFavoritesWhenExecuted() {
        // Test case fixture
        val repoResult = Single.just(mockk<Result<Unit>>())
        every { repository.addToFavorites(any()) } returns repoResult

        // Given an initialized use case

        // When use case is executed with suitable request data
        val request = AddFavoriteMovieRequest("id")
        val useCaseResult = useCase.execute(request)

        // Then use case should ask movie repository to add movie to favorites
        verify { repository.addToFavorites(request.movieId) }

        // And return repo result
        assertThat(useCaseResult).isEqualTo(repoResult)
    }
}