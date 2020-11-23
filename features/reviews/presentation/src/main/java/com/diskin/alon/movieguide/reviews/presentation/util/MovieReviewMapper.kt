package com.diskin.alon.movieguide.reviews.presentation.util

import android.content.res.Resources
import com.diskin.alon.movieguide.common.appservices.Result
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
        return source.map { res ->
            when(res){
                is Result.Success -> {
                    val dto = res.data
                    Result.Success(
                        MovieReview(
                            dto.id,
                            dto.title,
                            dto.rating.toString(),
                            dto.genres.joinToString(","),
                            LocalDateTime(dto.releaseDate).toString(resources.getString(
                                R.string.movie_release_date_format)),
                            dto.summary,
                            dto.review,
                            dto.backDropImageUrl,
                            dto.webUrl,
                            dto.trailers.map { Trailer(it.url,it.thumbnailUrl) }
                        )
                    )
                }

                is Result.Error -> Result.Error(res.error)
            }
        }
    }
}