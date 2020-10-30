package com.diskin.alon.movieguide.reviews.data

import com.diskin.alon.movieguide.common.common.Mapper
import com.diskin.alon.movieguide.reviews.domain.MovieEntity
import org.joda.time.LocalDate

class MovieEntityMapper : Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> {

    override fun map(source: List<MoviesResponse.MovieResponse>): List<MovieEntity> {
        return source.map { movieResponse ->
            MovieEntity(
                movieResponse.id.toString(),
                movieResponse.title,
                movieResponse.popularity,
                movieResponse.vote_average,
                LocalDate.parse(movieResponse.release_date).toDate().time,
                MOVIE_DB_BASE_POSTER_PATH.plus(movieResponse.poster_path)
            )
        }
    }
}