package com.diskin.alon.movieguide.reviews.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.data.implementations.MovieReviewRepositoryImpl
import com.diskin.alon.movieguide.reviews.data.remote.RemoteMovieReviewSource
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

/**
 * [MovieReviewRepositoryImpl] unit test class.
 */
class MovieReviewRepositoryImplTest {

    // Test subject
    private lateinit var repository: MovieReviewRepositoryImpl

    // Collaborators
    private val remoteSource: RemoteMovieReviewSource = mockk()

    @Before
    fun setUp() {
        // Init test subject
        repository = MovieReviewRepositoryImpl(remoteSource)
    }

    @Test
    fun fetchMovieReviewWhenQueried() {
        // Test case fixture
        val remoteReview = mockk<Result<MovieReviewEntity>>()
        every { remoteSource.getReview(any()) } returns Single.just(remoteReview)

        // Given an initialized repository

        // When repository is asked to retrieve a movie review
        val id = "id"
        val testObserver = repository.getReview(id).test()

        // Then repository should retrieve movie review from remote reviews data source
        verify { remoteSource.getReview(id) }

        // And return remote review
        testObserver.assertValue(remoteReview)
    }
}