package com.diskin.alon.movieguide.reviews.data.local

import com.diskin.alon.movieguide.common.util.Mapper
import com.diskin.alon.movieguide.reviews.domain.entities.MovieEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteMovieMapper @Inject constructor() : Mapper<MovieEntity, FavoriteMovie> {

    override fun map(source: MovieEntity): FavoriteMovie {
        return FavoriteMovie(
            source.id,
            source.title,
            source.popularity,
            source.rating,
            source.releaseDate,
            source.posterUrl
        )
    }
}