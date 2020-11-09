package com.diskin.alon.movieguide.reviews.appservices.usecase

import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity

/**
 * Map domain review entities to app services data models.
 */
class MovieReviewDtoMapper : Mapper<Result<MovieReviewEntity>, Result<MovieReviewDto>> {
    override fun map(source: Result<MovieReviewEntity>): Result<MovieReviewDto> {
        return when(source) {
            is Result.Success -> {
                Result.Success(
                    MovieReviewDto(
                        source.data.id,
                        source.data.title,
                        source.data.rating,
                        source.data.releaseDate,
                        source.data.backDropImageUrl,
                        source.data.genres.map { it.name },
                        source.data.summary,
                        source.data.review,
                        source.data.trailersUrl
                    )
                )
            }

            is Result.Error -> Result.Error(source.error)
        }
    }
}