package com.diskin.alon.movieguide.reviews.appservices.interfaces

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.reactivex.Observable

/**
 * [MovieReviewEntity] repository contract.
 */
interface MovieReviewRepository {

    fun getReview(id: String): Observable<Result<MovieReviewEntity>>
}
