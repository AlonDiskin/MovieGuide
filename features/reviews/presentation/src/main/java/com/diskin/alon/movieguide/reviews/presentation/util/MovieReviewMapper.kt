package com.diskin.alon.movieguide.reviews.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.appservices.Result
import com.diskin.alon.movieguide.common.appservices.mapResult
import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.appservices.data.MovieReviewDto
import com.diskin.alon.movieguide.reviews.presentation.R
import com.diskin.alon.movieguide.reviews.presentation.data.MovieReview
import com.diskin.alon.movieguide.reviews.presentation.data.Trailer
import io.reactivex.Observable
import org.joda.time.LocalDateTime
import javax.inject.Inject

/**
 * Maps app services movie review data model, to presentation data model.
 */
class MovieReviewMapper @Inject constructor(
    private val resources: Resources
) : Mapper<Observable<Result<MovieReviewDto>>,Observable<Result<MovieReview>>> {

    override fun map(source: Observable<Result<MovieReviewDto>>): Observable<Result<MovieReview>> {
        return source.mapResult{
            MovieReview(
                it.id,
                it.title,
                it.rating.toString(),
                it.genres.joinToString(","),
                LocalDateTime(it.releaseDate).toString(resources.getString(
                    R.string.movie_release_date_format)),
                it.summary,
                it.review,
                it.backDropImageUrl,
                it.webUrl,
                it.trailers.map { trailerDto ->  Trailer(trailerDto.url,trailerDto.thumbnailUrl) },
                it.favorite
            )
        }
    }
}