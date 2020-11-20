package com.diskin.alon.movieguide.reviews.data.implementations

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.data.remote.RemoteMovieReviewSource
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Handles data sources operations to provide [MovieReviewEntity]s.
 */
class MovieReviewRepositoryImpl @Inject constructor(
    private val remoteMovieReviewSource: RemoteMovieReviewSource
) : MovieReviewRepository {

    override fun getReview(id: String): Observable<Result<MovieReviewEntity>> {
        return remoteMovieReviewSource.getReview(id).toObservable()
    }
}
