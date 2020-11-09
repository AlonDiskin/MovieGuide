package com.diskin.alon.movieguide.reviews.presentation.data

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.presentation.ModelRequest
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewRequest
import io.reactivex.Observable

/**
 * Holds the data needed for model to service presentation movie review data.
 */
data class ReviewModelRequest(
    val movieId: String
) : ModelRequest<MovieReviewRequest,Observable<Result<MovieReview>>>(MovieReviewRequest(movieId))