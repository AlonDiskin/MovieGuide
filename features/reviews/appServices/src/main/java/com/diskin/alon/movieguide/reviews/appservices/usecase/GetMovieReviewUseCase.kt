package com.diskin.alon.movieguide.reviews.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewRequest
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations that retrieves movie review data.
 */
class GetMovieReviewUseCase @Inject constructor(
    private val repo: MovieReviewRepository,
    private val mapper: Mapper<Result<MovieReviewEntity>, Result<MovieReviewDto>>
) : UseCase<MovieReviewRequest,Observable<Result<MovieReviewDto>>>{

    override fun execute(param: MovieReviewRequest): Observable<Result<MovieReviewDto>> {
        return repo.getReview(param.id)
            .map(mapper::map)
    }
}