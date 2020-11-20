package com.diskin.alon.movieguide.reviews.data.remote

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.data.remote.data.MoviesResponse
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import org.joda.time.LocalDate

class MovieMapper : Mapper<List<MoviesResponse.MovieResponse>, List<MovieEntity>> {

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