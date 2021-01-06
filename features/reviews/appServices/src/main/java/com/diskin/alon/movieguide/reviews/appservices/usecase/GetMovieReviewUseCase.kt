package com.diskin.alon.movieguide.reviews.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.UseCase
import com.diskin.alon.movieguide.common.appservices.toData
import com.diskin.alon.movieguide.common.appservices.toResult
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewRequest
import com.diskin.alon.movieguide.reviews.appservices.data.TrailerDto
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieRepository
import com.diskin.alon.movieguide.reviews.appservices.interfaces.MovieReviewRepository
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Coordinate app operations that retrieves movie review data.
 */
class GetMovieReviewUseCase @Inject constructor(
    private val reviewRepo: MovieReviewRepository,
    private val movieRepository: MovieRepository
) : UseCase<MovieReviewRequest,Observable<Result<MovieReviewDto>>>{

    override fun execute(param: MovieReviewRequest): Observable<Result<MovieReviewDto>> {
        return Observable.combineLatest(
            reviewRepo.getReview(param.id).toData(),
            movieRepository.isFavorite(param.id).toData(),
            this::composeReviewDto
        ).toResult()
    }

    private fun composeReviewDto(reviewEntity: MovieReviewEntity, isFavorite: Boolean): MovieReviewDto {
        return MovieReviewDto(
            reviewEntity.id,
            reviewEntity.title,
            reviewEntity.rating,
            reviewEntity.releaseDate,
            reviewEntity.backDropImageUrl,
            reviewEntity.genres.map { it.name },
            reviewEntity.summary,
            reviewEntity.review,
            reviewEntity.webUrl,
            reviewEntity.trailersUrl.map {
                TrailerDto(it.url,it.thumbnailUrl)
            },
            isFavorite
        )
    }
}