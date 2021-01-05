package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import org.joda.time.LocalDate

class MovieMapper : Mapper<MoviesResponse.MovieResponse, MovieEntity> {

    override fun map(source: MoviesResponse.MovieResponse): MovieEntity {
        return MovieEntity(
            source.id.toString(),
            source.title ?: "",
            source.popularity ?: 0.0,
            source.vote_average ?: 0.0,
            LocalDate.parse(source.release_date).toDate().time,
            MOVIE_DB_BASE_POSTER_PATH.plus(source.poster_path)
        )
    }
}