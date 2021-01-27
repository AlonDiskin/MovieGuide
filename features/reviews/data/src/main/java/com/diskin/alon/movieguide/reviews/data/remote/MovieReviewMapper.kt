package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.util.Mapper2
import com.diskin.alon.movieguide.reviews.data.remote.data.MovieDetailResponse
import com.diskin.alon.movieguide.reviews.data.remote.data.TrailersResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieReviewEntity
import com.diskin.alon.movieguide.reviews.domain.value.MovieGenre
import com.diskin.alon.movieguide.reviews.domain.value.Trailer
import org.joda.time.LocalDate
import javax.inject.Inject

/**
 * Map remote data model of movie reviews data,to domain data model.
 */
class MovieReviewMapper @Inject constructor() : Mapper2<MovieDetailResponse, TrailersResponse, MovieReviewEntity> {

    companion object {
        private const val REVIEW_STUB = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur tempor suscipit ipsum, at " +
                "blandit nisi. Aliquam erat volutpat. Nulla at mauris mattis, pellentesque neque vitae, consequat augue. Ut mollis vel leo" +
                " in malesuada. Proin vitae fringilla mauris. Quisque placerat sodales quam quis gravida. Pellentesque sollicitudin vestibulum" +
                " arcu, eget imperdiet nulla pellentesque sit amet."
    }

    override fun map(source1: MovieDetailResponse, source2: TrailersResponse): MovieReviewEntity {
        return MovieReviewEntity(
            source1.id.toString(),
            source1.title,
            source1.vote_average,
            LocalDate.parse(source1.release_date).toDate().time,
            MOVIE_DB_BASE_BACKDROP_PATH.plus(source1.backdrop_path),
            source1.genres.map { MovieGenre(it.name) },
            source1.overview,
            REVIEW_STUB,
            MOVIE_DB_BASE.plus(source1.id.toString()),
            source2.results.map {
                Trailer(
                    YOUTUBE_BASE_PATH.plus(it.key),
                    YOUTUBE_THUMBNAIL_PATH.plus(it.key).plus(YOUTUBE_THUMBNAIL_PATH_END)
                )
            }
        )
    }
}
