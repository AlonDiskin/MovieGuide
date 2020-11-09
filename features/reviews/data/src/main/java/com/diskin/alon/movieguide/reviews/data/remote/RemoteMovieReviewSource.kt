package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.reactivex.Single

/**
 * Remote [MovieReviewEntity] data source contract.
 */
interface RemoteMovieReviewSource {

    fun getReview(id: String): Single<Result<MovieReviewEntity>>
}
